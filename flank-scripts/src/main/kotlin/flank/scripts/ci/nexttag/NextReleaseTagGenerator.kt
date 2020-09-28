package flank.scripts.ci.nexttag

import java.time.LocalDate
import java.time.Year
import java.time.format.DateTimeFormatter

fun generateNextReleaseTag(previousReleaseTag: String): String {
    val (year, month, number) = previousReleaseTag.trimStart('v').split('.')
    return if (isNextReleaseInCurrentMonth(
            year,
            month
        )
    ) "v$year.$month.${number.toInt() + 1}" else currentMonthFirstTag()
}

private fun isNextReleaseInCurrentMonth(year: String, month: String) =
    DateTimeFormatter.ofPattern("yy").format(Year.now()) == year &&
        DateTimeFormatter.ofPattern("MM").format(LocalDate.now()) == month

private fun currentMonthFirstTag() = "v${DateTimeFormatter.ofPattern("yy.MM").format(LocalDate.now())}.0"
