package ftl.adapter.google

import ftl.api.FileReference
import ftl.config.FtlConstants

internal fun String.toApiModel(fileReference: FileReference) = fileReference.copy(local = this)

internal fun String.asFileReference(): FileReference =
    if (startsWith(FtlConstants.GCS_PREFIX)) FileReference(remote = this) else FileReference(local = this)
