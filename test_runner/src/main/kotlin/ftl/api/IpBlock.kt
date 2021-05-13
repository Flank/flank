package ftl.api

import ftl.adapter.GoogleIpBlockFetch

val fetchIpBlocks: IpBlockList.Fetch get() = GoogleIpBlockFetch

data class IpBlockList(
    val blocks: List<IpBlock>
) {

    data class IpBlock(
        val block: String,
        val form: String,
        val addedDate: String
    )

    interface Fetch : () -> IpBlockList
}
