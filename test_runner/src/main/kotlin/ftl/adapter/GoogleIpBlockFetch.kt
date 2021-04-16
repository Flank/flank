package ftl.adapter

import ftl.data.IpBlock
import ftl.adapter.google.ipBlocksList

object GoogleIpBlockFetch :
    IpBlock.Fetch,
    () -> List<IpBlock> by {
        ipBlocksList()
    }
