package ftl.reports.api

import com.google.api.services.toolresults.model.Duration
import com.google.api.services.toolresults.model.Timestamp
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

internal fun Double.format(): String = "%.3f".format(Locale.ROOT, this)

internal fun Int?.format() = (this ?: 0).toString()

internal fun Duration?.format(): String =
    if (this == null)
        "0.0" else
        ((seconds ?: 0) + nanosToSeconds(nanos)).toString()

//   manually divide to keep fractional precision
private fun nanosToSeconds(nanos: Int?): Double =
    if (nanos == null)
        0.0 else
        nanos / 1E9

private val utcDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").apply {
    timeZone = TimeZone.getTimeZone("UTC")
}

fun Timestamp.asUnixTimestamp() = seconds * 1_000 + nanos / 1_000_000

fun Long.formatUtcDate() = utcDateFormat.format(this)!!
