package io.github.theapache64.perfboy.traceparser.core

interface ProfileData {
    val name: String
    val level: Int
    val threadStartTimeInMillisecond: Double
    val globalStartTimeInMillisecond: Double
    val threadEndTimeInMillisecond: Double
    val globalEndTimeInMillisecond: Double
    val threadSelfTime: Double
    val globalSelfTime: Double
    val parent: ProfileData?
    val children: List<ProfileData>
}
