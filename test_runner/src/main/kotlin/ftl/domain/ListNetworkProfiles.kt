package ftl.domain

import flank.common.logLn
import ftl.environment.networkConfigurationAsTable

interface ListNetworkProfiles

operator fun ListNetworkProfiles.invoke() {
    logLn("fetching available network profiles...")
    logLn(networkConfigurationAsTable())
}
