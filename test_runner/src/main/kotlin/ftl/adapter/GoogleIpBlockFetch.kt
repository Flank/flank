package ftl.adapter

import ftl.adapter.environment.ipBlocksList
import ftl.data.IpBlock

object GoogleIpBlockFetch :
    IpBlock.Fetch,
    () -> List<IpBlock> by {
        ipBlocksList()
    }
