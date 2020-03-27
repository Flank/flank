package ftl.reports.api

import com.google.api.services.toolresults.model.Duration
import com.google.api.services.toolresults.model.TestCase
import com.google.api.services.toolresults.model.Timestamp
import ftl.util.mutableMapProperty
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

internal fun Double.format(): String = "%.3f".format(Locale.ROOT, this)

internal fun Int?.format() = (this ?: 0).toString()

internal fun Duration?.millis(): Double =
    if (this == null)
        0.0 else
        ((seconds ?: 0) + nanosToSeconds(nanos))

//   manually divide to keep fractional precision
private fun nanosToSeconds(nanos: Int?): Double =
    if (nanos == null)
        0.0 else
        nanos / 1E9

private val utcDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").apply {
    timeZone = TimeZone.getTimeZone("UTC")
}

fun Timestamp.asUnixTimestamp() = (seconds ?: 0) * 1_000 + (nanos ?: 0) / 1_000_000

fun Long.formatUtcDate() = utcDateFormat.format(this)!!

var TestCase.flaky: Boolean by mutableMapProperty { false }
