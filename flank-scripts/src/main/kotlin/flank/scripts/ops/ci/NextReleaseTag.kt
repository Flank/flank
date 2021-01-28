package flank.scripts.ops.ci

import com.github.kittinunf.result.Result
import flank.scripts.data.github.getLatestReleaseTag
import kotlinx.coroutines.runBlocking
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import kotlin.system.exitProcess

fun createNextReleaseTag(token: String) = runBlocking {
    when (val result = getLatestReleaseTag(token)) {
        is Result.Success -> println(generateNextReleaseTag(result.value.tag))
        is Result.Failure -> {
            println(result.error)
            exitProcess(1)
        }
    }
}

internal fun generateNextReleaseTag(previousReleaseTag: String): String {
    val (year, month, number) = previousReleaseTag.trimStart('v').split('.')
    return if (isNextReleaseInCurrentMonth(
            year,
            month
        )
    ) "v$year.$month.${number.toInt() + 1}" else currentMonthFirstTag()
}

private fun isNextReleaseInCurrentMonth(year: String, month: String) =
    DateTimeFormatter.ofPattern("yy").format(LocalDate.now()) == year &&
        DateTimeFormatter.ofPattern("MM").format(LocalDate.now()) == month

private fun currentMonthFirstTag() = "v${DateTimeFormatter.ofPattern("yy.MM").format(LocalDate.now())}.0"
