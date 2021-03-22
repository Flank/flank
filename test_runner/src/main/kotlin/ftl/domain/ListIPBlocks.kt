package ftl.domain

import flank.common.logLn
import ftl.environment.ipBlocksListAsTable

interface ListIPBlocks

operator fun ListIPBlocks.invoke() {
    logLn(ipBlocksListAsTable())
}
