package ftl.domain

import ftl.api.fetchSoftwareCatalog
import ftl.presentation.Output

interface ListProvidedSoftware : Output

operator fun ListProvidedSoftware.invoke() {
    fetchSoftwareCatalog().out()
}
