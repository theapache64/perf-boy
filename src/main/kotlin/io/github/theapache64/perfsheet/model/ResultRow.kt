package io.github.theapache64.perfsheet.model

sealed interface ResultRow {
    data class SingleTrace(
        val name: String,
        val durationInMs: Long,
        val count: Int,
        val threadDetails: List<ThreadDetail>,
        var comparison: String?
    ) : ResultRow

    data class DualTrace(
        val name: String,
        val beforeDurationInMs: Long,
        val afterDurationInMs: Long,
        val diffInMs: Long,
        val beforeCount: Int,
        val afterCount: Int,
        val countComparison: Int,
        val beforeThreadDetails: List<ThreadDetail>,
        val afterThreadDetails: List<ThreadDetail>,
        var beforeComparison: String?,
        var afterComparison: String?
    ) : ResultRow

    data class SingleFrame(
        val name: String,
        val durationInMs: Long,
    ) : ResultRow

    data class DualFrame(
        val name: String,
        val beforeDurationInMs: Long,
        val afterDurationInMs: Long,
        val diffInMs: Long,
    ) : ResultRow
}


data class ThreadDetail(
    val threadName: String,
    var noOfBlocks: Int,
    var totalDurationInMs: Double,
)