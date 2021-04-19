package ftl.api

import ftl.adapter.GoogleIpBlockFetch

val fetchIpBlocks: IpBlock.Fetch get() = GoogleIpBlockFetch

data class IpBlock(
    val block: String,
    val form: String,
    val addedDate: String
) {

    interface Fetch : () -> List<IpBlock>
}
