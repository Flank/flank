@file:Suppress("EXPERIMENTAL_API_USAGE")

package ftl.util

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.map

fun <T> Flow<T>.repeat(times: Int): Flow<T> =
    flatMapConcat { next -> (0 until times).asFlow().map { next } }
