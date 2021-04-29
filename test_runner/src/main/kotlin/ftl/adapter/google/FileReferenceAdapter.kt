package ftl.adapter.google

import ftl.api.FileReference

internal fun String.toApiModel(fileReference: FileReference) = fileReference.copy(local = this)
