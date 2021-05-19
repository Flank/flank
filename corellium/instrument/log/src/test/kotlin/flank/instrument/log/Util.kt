package flank.instrument.log

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow

fun flowLogs(name: String): Flow<String> =
    Unit.javaClass.classLoader
        .getResourceAsStream(name)!!
        .bufferedReader()
        .lineSequence()
        .asFlow()

const val LOG = "example_android_logs_0.txt"
const val LOG1 = "example_android_logs_1.txt"
const val LOG2 = "example_android_logs_2.txt"
const val LOG3 = "example_android_logs_3.txt"
