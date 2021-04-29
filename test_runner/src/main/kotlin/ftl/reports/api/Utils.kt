package ftl.reports.api

import com.google.api.services.toolresults.model.Duration
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

internal fun Double.format(): String = "%.3f".format(Locale.ROOT, this)

internal fun Int?.format() = (this ?: 0).toString()

internal fun Int.twoDigitString() = toString().padStart(2, '0')

internal fun Duration?.millis(): Double =
    if (this == null)
        0.0 else
        ((seconds ?: 0) + nanosToSeconds(nanos))

//   manually divide to keep fractional precision
private fun nanosToSeconds(nanos: Int?): Double =
    if (nanos == null)
        0.0 else
        nanos / 1E9

val utcDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").apply {
    timeZone = TimeZone.getTimeZone("UTC")
}
