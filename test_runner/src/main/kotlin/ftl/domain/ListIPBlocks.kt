package ftl.domain

import flank.common.logLn
import ftl.adapter.environment.toCliTable
import ftl.data.fetchIpBLocks

interface ListIPBlocks

operator fun ListIPBlocks.invoke() {
    // TODO move toCliTable() to presentation layer during refactor of presentation after #1728
    logLn(fetchIpBLocks().toCliTable())
}
