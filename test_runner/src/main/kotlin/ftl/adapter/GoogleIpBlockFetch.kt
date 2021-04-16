package ftl.adapter

import ftl.adapter.google.deviceIPBlocks
import ftl.data.IpBlock
import ftl.adapter.google.prettyDate

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
