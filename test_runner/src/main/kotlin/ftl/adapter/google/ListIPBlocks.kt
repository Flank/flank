package ftl.adapter.google

import com.google.testing.model.Date
import ftl.data.IpBlock
import ftl.gc.deviceIPBlocks
import ftl.reports.api.twoDigitString

fun ipBlocksList(): List<IpBlock> = deviceIPBlocks().map { deviceIpBlock ->
    IpBlock(
        block = deviceIpBlock.block ?: UNABLE,
        form = deviceIpBlock.form ?: UNABLE,
        addedDate = deviceIpBlock.addedDate?.prettyDate() ?: UNABLE
    )
}

// yyyy-mm-dd
private fun Date.prettyDate() =
    if (listOf(year, month, day).any { it == null }) null
    else "$year-${month.twoDigitString()}-${day.twoDigitString()}"

private const val UNABLE = "[Unable to fetch]"
