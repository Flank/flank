package ftl.adapter

import ftl.data.IpBlock
import ftl.environment.ipBlocksList

object GoogleIpBlockFetch :
    IpBlock.Fetch,
    () -> List<IpBlock> by {
        ipBlocksList()
    }
