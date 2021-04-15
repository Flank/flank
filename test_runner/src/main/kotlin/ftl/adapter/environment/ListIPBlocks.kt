package ftl.adapter.environment

import com.google.testing.model.Date
import com.google.testing.model.DeviceIpBlock
import ftl.data.IpBlock
import ftl.gc.deviceIPBlocks
import ftl.reports.api.twoDigitString
import java.util.Objects

fun ipBlocksList() = deviceIPBlocks().toIpBlocks()

private fun List<DeviceIpBlock>.toIpBlocks(): List<IpBlock> =
    map { IpBlock(it.block.orUnable, it.form.orUnable, addedDate = it.addedDate.prettyDate) }

private val String?.orUnable
    get() = this ?: UNABLE

// yyyy-mm-dd
private val Date?.prettyDate
    get() = this?.run {
        if (allNotNull(year, month, day)) "$year-${month.twoDigitString()}-${day.twoDigitString()}" else null
    }
        ?: UNABLE

private fun allNotNull(vararg nullable: Any?) = nullable.none(Objects::isNull)

private const val UNABLE = "[Unable to fetch]"
