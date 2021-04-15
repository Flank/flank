package ftl.data

import ftl.adapter.GoogleIpBlockFetch

val fetchIpBLocks: IpBlock.Fetch get() = GoogleIpBlockFetch

data class IpBlock(
    val block: String,
    val form: String,
    val addedDate: String
) {

    interface Fetch : () -> List<IpBlock>
}
