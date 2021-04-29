package ftl.adapter

import ftl.adapter.google.toApiModel
import ftl.api.FileReference
import ftl.client.google.fileReferenceDownload

object GcStorageDownload :
    FileReference.Download,
    (FileReference, Boolean, Boolean) -> FileReference by { fileReference, ifNeeded, ignoreErrors ->
        fileReferenceDownload(fileReference, ifNeeded, ignoreErrors).toApiModel(fileReference)
    }
