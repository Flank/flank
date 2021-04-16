package ftl.environment

import ftl.data.IpBlock
import ftl.util.TableColumn
import ftl.util.TableStyle

// TODO move to presentation layer during refactor of presentation after #1728
fun List<IpBlock>.toCliTable() = createDataMap()
    .collectDataPerColumn()
    .buildTable()

private fun List<IpBlock>.createDataMap() = fold(mutableMapOf<String, MutableList<String>>()) { map, ipBlock ->
    map.apply {
        getOrCreateList(IP_BLOCK).add(ipBlock.block)
        getOrCreateList(IP_FORM).add(ipBlock.form)
        getOrCreateList(IP_ADDED_DATE).add(ipBlock.addedDate)
    }
}

private fun Map<String, List<String>>.collectDataPerColumn() = map { (header, data) -> TableColumn(header, data) }

private fun List<TableColumn>.buildTable() =
    if (isNotEmpty()) ftl.util.buildTable(*toTypedArray(), tableStyle = TableStyle.DEFAULT)
    else "--Flank was unable to get data from TestLab--"

private const val IP_BLOCK = "BLOCK"
private const val IP_FORM = "FORM"
private const val IP_ADDED_DATE = "ADDED_DATE"
