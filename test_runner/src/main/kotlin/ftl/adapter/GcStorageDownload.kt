package ftl.adapter

import ftl.adapter.google.toApiModel
import ftl.api.FileReference
import ftl.client.google.gcStorageDownload
import ftl.run.exception.FlankGeneralError

object GcStorageDownload :
    FileReference.Download,
    (FileReference, Boolean, Boolean) -> FileReference by { fileReference, ifNeeded, ignoreErrors ->
        if (!ignoreErrors) {
            if (fileReference.local.isBlank() && fileReference.remote.isBlank()) throw FlankGeneralError("Cannot create empty FileReference")
        }
        if (ifNeeded) {
            if (fileReference.local.isNotBlank()) fileReference
            else gcStorageDownload(fileReference.remote, ignoreErrors).toApiModel(fileReference)
        } else {
            gcStorageDownload(fileReference.remote, ignoreErrors).toApiModel(fileReference)
        }
    }
