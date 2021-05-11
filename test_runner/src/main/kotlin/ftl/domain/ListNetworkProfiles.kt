package ftl.domain

import ftl.api.fetchNetworkProfiles
import ftl.presentation.Output

interface ListNetworkProfiles : Output

operator fun ListNetworkProfiles.invoke() {
    "fetching available network profiles...".out()
    fetchNetworkProfiles().out()
}
