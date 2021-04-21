package ftl.adapter

import ftl.adapter.google.toApiModel
import ftl.api.IpBlock
import ftl.client.google.deviceIPBlocks

object GoogleIpBlockFetch :
    IpBlock.Fetch,
    () -> List<IpBlock> by {
        deviceIPBlocks().toApiModel()
    }
