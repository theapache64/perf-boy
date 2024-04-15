package io.github.theapache64.perfsheet.data.repo

import io.github.theapache64.perfsheet.model.FinalResult
import io.github.theapache64.perfsheet.model.Method
import io.github.theapache64.perfsheet.model.Node
import io.github.theapache64.perfsheet.traceparser.analyzer.AnalyzerResultImpl
import io.github.theapache64.perfsheet.traceparser.analyzer.TraceAnalyzer
import io.github.theapache64.perfsheet.traceparser.core.AnalyzerResult
import java.io.File
import javax.inject.Inject
import kotlin.math.roundToLong

enum class FocusArea {
    ALL_THREADS,
    MAIN_THREAD_ONLY,
    BACKGROUND_THREADS_ONLY
}

interface TraceRepo {
    fun init(beforeTrace: File, afterTrace: File)
    fun parse(focusArea: FocusArea): Map<String, FinalResult>
}

class TraceRepoImpl @Inject constructor(
    private val traceAnalyzer: TraceAnalyzer
) : TraceRepo {

    companion object{
        private const val NOT_PRESENT = "not present"
    }

    private lateinit var beforeAnalysisResult: AnalyzerResultImpl
    private lateinit var afterAnalysisResult: AnalyzerResultImpl

    override fun init(beforeTrace: File, afterTrace: File) {
        this.beforeAnalysisResult = traceAnalyzer.analyze(beforeTrace)
        this.afterAnalysisResult = traceAnalyzer.analyze(afterTrace)
    }

    override fun parse(
        focusArea: FocusArea,
    ): Map<String, FinalResult> {
        val finalResult = mutableMapOf<String, FinalResult>()
        val beforeMap = beforeAnalysisResult.toMap(focusArea)
        val afterMap = afterAnalysisResult.toMap(focusArea)

        val methodNames = beforeMap.keys + afterMap.keys
        for (methodName in methodNames) {
            val beforeMethod = beforeMap[methodName]
            val afterMethod = afterMap[methodName]
            val diffInMs = calculateDiff(beforeMethod, afterMethod)

            val beforeCount = beforeMethod?.nodes?.size ?: -1
            val afterCount = afterMethod?.nodes?.size ?: -1
            val countLabel = calculateCountLabel(beforeCount, afterCount)

            val beforeThreadDetails = calculateThreadDetails(beforeMethod)
            val afterThreadDetails = calculateThreadDetails(afterMethod)

            finalResult[methodName] = FinalResult(
                name = methodName,
                beforeDurationInMs = (beforeMethod?.nodes?.sumOf { it.durationInMs } ?: -1).toLong(),
                afterDurationInMs = (afterMethod?.nodes?.sumOf { it.durationInMs } ?: -1).toLong(),
                diffInMs = diffInMs,
                beforeCount = beforeCount,
                afterCount = afterCount,
                countComparison = """
                    Before: ${beforeCount.takeIf { it >= 1 } ?: NOT_PRESENT}
                    After: ${afterCount.takeIf { it >= 1 } ?: NOT_PRESENT}
                    
                    $countLabel
                """.trimIndent(),
                beforeThreadDetails = beforeThreadDetails,
                afterThreadDetails = afterThreadDetails,
                beforeComparison = summarise(
                    focusArea = focusArea,
                    before = beforeThreadDetails,
                    compareWith = null
                ).ifBlank { NOT_PRESENT },
                afterComparison = summarise(
                    focusArea = focusArea,
                    before = afterThreadDetails,
                    compareWith = beforeThreadDetails
                ).ifBlank { NOT_PRESENT }

            )
        }
        return finalResult.entries.sortedByDescending { it.value.diffInMs.toLong() }.associateBy({ it.key }, { it.value })
    }




    private fun Long.notPresentIfMinusOne(): String {
        if (this == -1L) return NOT_PRESENT
        return this.toString()
    }

    private fun summarise(
        focusArea: FocusArea,
        before: List<FinalResult.ThreadDetail>,
        compareWith: List<FinalResult.ThreadDetail>?
    ): String {

        return before.joinToString(separator = "\n") { beforeThread ->
            val threadName = if (focusArea != FocusArea.MAIN_THREAD_ONLY) {
                "🧵 ${beforeThread.threadName}, "
            } else {
                ""
            }
            val summary =
                "${threadName}⏱️${beforeThread.totalDurationInMs.roundToLong()}ms, ⏹︎ (${beforeThread.noOfBlocks} ${if (beforeThread.noOfBlocks > 1) "blocks" else "block"})"
            if (compareWith == null) {
                summary
            } else {
                val beforeDuration =
                    compareWith.find { afterThread -> afterThread.threadName == beforeThread.threadName }?.totalDurationInMs?.roundToLong()
                        ?: 0
                val afterDuration = beforeThread.totalDurationInMs.roundToLong()
                val durationDiff = afterDuration - beforeDuration
                // if negative '-' else +, if zero nothing
                val sign = if (durationDiff > 0) "+" else ""

                val beforeBlocks =
                    compareWith.find { afterThread2 -> afterThread2.threadName == beforeThread.threadName }?.noOfBlocks
                        ?: 0
                val afterBlocks = beforeThread.noOfBlocks
                val blocksDiff = afterBlocks - beforeBlocks
                val blockSign = if (blocksDiff > 0) "+" else ""

                val comparison = "Change: $sign${durationDiff}ms, $blockSign${blocksDiff} blocks"

                """
                $summary
                $comparison
            """.trimIndent()
            }
        }
    }

    private fun calculateThreadDetails(beforeMethod: Method?): List<FinalResult.ThreadDetail> {
        val threadDetails = mutableListOf<FinalResult.ThreadDetail>()
        for (threadNode in beforeMethod?.nodes ?: emptyList()) {
            var threadDetail = threadDetails.find { it.threadName == threadNode.threadName }
            if (threadDetail == null) {
                // first detail node
                threadDetail = FinalResult.ThreadDetail(threadNode.threadName, noOfBlocks = 0, 0.0)
                threadDetails.add(threadDetail)
            }
            threadDetail.noOfBlocks++
            threadDetail.totalDurationInMs += threadNode.durationInMs
        }

        // compare by total duration but if there's threadName == "main", it should be always first
        return threadDetails.sortedByDescending {
            if (it.threadName == "main") {
                Long.MAX_VALUE
            } else {
                it.totalDurationInMs.toLong()
            }
        }
    }

    private fun calculateCountLabel(beforeCount: Int, afterCount: Int): String {
        return when {
            beforeCount == afterCount -> "0"
            beforeCount > 0 && afterCount == -1 -> "Removed ($beforeCount)"
            beforeCount == -1 && afterCount > 0 -> "Added ($afterCount)"
            else -> {
                val diff = (afterCount - beforeCount)
                when {
                    diff > 0 -> "$diff (added)"
                    else -> "$diff (removed)"
                }
            }
        }
    }

    private fun calculateDiff(beforeMethod: Method?, afterMethod: Method?): Long {
        return when {
            beforeMethod != null && afterMethod != null -> {
                afterMethod.nodes.sumOf {
                    it.durationInMs
                } - beforeMethod.nodes.sumOf {
                    it.durationInMs
                }
            }

            afterMethod != null -> {
                afterMethod.nodes.sumOf { it.durationInMs }
            }

            beforeMethod != null -> {
                -beforeMethod.nodes.sumOf { it.durationInMs }
            }

            else -> 0
        }.toLong()
    }

    private fun AnalyzerResult.toMap(focusArea: FocusArea): Map<String, Method> {
        val resultMap = mutableMapOf<String, Method>()
        for ((threadId, allMethods) in this.data) {
            val thread = this.threads.find { it.threadId == threadId } ?: error("Thread not found: '$threadId'")

            when (focusArea) {
                FocusArea.MAIN_THREAD_ONLY -> if (thread.threadId != mainThreadId) continue
                FocusArea.BACKGROUND_THREADS_ONLY -> if (thread.threadId == mainThreadId) continue
                FocusArea.ALL_THREADS -> {
                    // all threads pls
                }
            }

            for (method in allMethods) {
                val methodName = method.name
                val traceMethod = resultMap.getOrPut(
                    methodName
                ) {
                    Method(
                        name = methodName,
                        nodes = mutableListOf()
                    )
                }

                val duration = method.threadEndTimeInMillisecond - method.threadStartTimeInMillisecond
                traceMethod.nodes.add(
                    Node(
                        threadName = thread.name,
                        durationInMs = duration
                    )
                )
            }
        }

        return resultMap
    }
}