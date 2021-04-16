package ftl.adapter.google

import com.google.testing.model.Date
import ftl.gc.GcTesting
import ftl.http.executeWithRetry
import ftl.reports.api.twoDigitString

internal fun deviceIPBlocks() = GcTesting.get.testEnvironmentCatalog()
    .get("DEVICE_IP_BLOCKS")
    .executeWithRetry()
    ?.deviceIpBlockCatalog
    ?.ipBlocks
    .orEmpty()

// yyyy-mm-dd
internal fun Date.prettyDate() =
    if (listOf(year, month, day).any { it == null }) null
    else "$year-${month.twoDigitString()}-${day.twoDigitString()}"
