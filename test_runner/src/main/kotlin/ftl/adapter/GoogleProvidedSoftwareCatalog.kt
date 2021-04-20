package ftl.adapter

import com.google.testing.model.ProvidedSoftwareCatalog
import ftl.adapter.google.getProvidedSoftwareAsATableToApiModel
import ftl.adapter.google.getProvidedSoftwareToApiModel
import ftl.api.SoftwareCatalog

object GoogleProvidedSoftwareCatalog :
    SoftwareCatalog.Fetch,
    () -> ProvidedSoftwareCatalog by {
        getProvidedSoftwareToApiModel()
    }

object GoogleProvidedSoftwareCatalogAsTable :
    SoftwareCatalog.FetchAsTable,
    () -> String by {
        getProvidedSoftwareAsATableToApiModel()
    }
