package ftl.adapter

import com.google.api.services.testing.model.ProvidedSoftwareCatalog
import ftl.adapter.google.getProvidedSoftwareToApiModel
import ftl.api.SoftwareCatalog

object GoogleProvidedSoftwareCatalog :
    SoftwareCatalog.Fetch,
    () -> ProvidedSoftwareCatalog by {
        getProvidedSoftwareToApiModel()
    }
