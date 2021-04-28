package ftl.util

import ftl.api.FileReference
import ftl.args.IArgs
import ftl.config.FtlConstants

fun String.asFileReference(): FileReference =
    if (startsWith(FtlConstants.GCS_PREFIX)) FileReference(remote = this) else FileReference(local = this)

fun IArgs.getSmartFlankGCSPathAsFileReference() = this.smartFlankGcsPath.asFileReference()
