package ftl.domain

import ftl.api.fetchIpBlocks
import ftl.presentation.Output

interface ListIPBlocks : Output

operator fun ListIPBlocks.invoke() {
    fetchIpBlocks().out()
}
