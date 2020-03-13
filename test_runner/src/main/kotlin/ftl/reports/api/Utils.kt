package ftl.reports.api

import com.google.api.services.toolresults.model.Duration
import java.util.*

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
