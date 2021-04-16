package ftl.adapter

import ftl.adapter.google.deviceIPBlocks
import ftl.adapter.google.prettyDate
import ftl.data.IpBlock

object GoogleIpBlockFetch :
    IpBlock.Fetch,
    () -> List<IpBlock> by {
        deviceIPBlocks().map { deviceIpBlock ->
            IpBlock(
                block = deviceIpBlock.block ?: UNABLE,
                form = deviceIpBlock.form ?: UNABLE,
                addedDate = deviceIpBlock.addedDate?.prettyDate() ?: UNABLE
            )
        }
    }

private const val UNABLE = "[Unable to fetch]"
