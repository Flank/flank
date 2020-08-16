package ftl.environment

import com.google.api.services.testing.model.Date
import com.google.api.services.testing.model.DeviceIpBlock
import ftl.gc.deviceIPBlocks
import ftl.util.TableColumn
import ftl.util.TableStyle
import ftl.util.buildTable
import java.util.Objects.isNull

fun ipBlocksListAsTable() = deviceIPBlocks()
    .toNullProof()
    .createDataMap()
    .collectDataPerColumn()
    .buildTable()

private fun List<DeviceIpBlock>.toNullProof() =
    map { IpBlocksNonNull(it.block.orUnable, it.form.orUnable, addedDate = it.addedDate.prettyDate) }

private fun List<IpBlocksNonNull>.createDataMap() = fold(mutableMapOf<String, MutableList<String>>()) { map, ipBlock ->
    map.apply {
        getOrCreateList(IP_BLOCK).add(ipBlock.block)
        getOrCreateList(IP_FORM).add(ipBlock.form)
        getOrCreateList(IP_ADDED_DATE).add(ipBlock.addedDate)
    }
}

private fun Map<String, List<String>>.collectDataPerColumn() = map { (header, data) -> TableColumn(header, data) }

private fun List<TableColumn>.buildTable() =
    if (isNotEmpty()) buildTable(*toTypedArray(), tableStyle = TableStyle.DEFAULT)
    else "--Flank was unable to get data from TestLab--"

// yyyy-mm-dd
private val Date?.prettyDate
    get() = this?.run { if (allNotNull(year, month, day)) "$year-${month.twoDigits}-${day.twoDigits}" else null }
        ?: UNABLE

private fun allNotNull(vararg nullable: Any?) = nullable.none { isNull(it) }

private val String?.orUnable
    get() = this ?: UNABLE

private val Int.twoDigits
    get() = if (this > 9) "$this" else "0$this"

private const val IP_BLOCK = "BLOCK"
private const val IP_FORM = "FORM"
private const val IP_ADDED_DATE = "ADDED_DATE"
private const val UNABLE = "[Unable to fetch]"

private data class IpBlocksNonNull(
    val block: String,
    val form: String,
    val addedDate: String
)