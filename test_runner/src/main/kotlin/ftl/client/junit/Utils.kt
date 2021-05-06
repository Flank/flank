package ftl.client.junit

import com.google.api.services.toolresults.model.TestCase
import com.google.api.services.toolresults.model.Timestamp
import ftl.reports.api.utcDateFormat
import ftl.util.mutableMapProperty

fun Timestamp.asUnixTimestamp() = (seconds ?: 0) * 1_000 + (nanos ?: 0) / 1_000_000

fun Long.formatUtcDate() = utcDateFormat.format(this)!!

var TestCase.flaky: Boolean by mutableMapProperty { false }
