package ftl.adapter

import ftl.adapter.google.toApiModel
import ftl.api.IpBlockList
import ftl.client.google.deviceIPBlocks

object GoogleIpBlockFetch :
    IpBlockList.Fetch,
    () -> IpBlockList by {
        deviceIPBlocks().toApiModel()
    }
