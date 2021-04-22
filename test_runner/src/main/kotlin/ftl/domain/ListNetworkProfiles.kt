package ftl.domain

import flank.common.logLn
import ftl.api.fetchNetworkProfiles
import ftl.environment.common.toCliTable

interface ListNetworkProfiles

operator fun ListNetworkProfiles.invoke() {
    // TODO move toCliTable() and printing presentation layer during refactor of presentation after #1728
    logLn("fetching available network profiles...")
    logLn(fetchNetworkProfiles().toCliTable())
}
