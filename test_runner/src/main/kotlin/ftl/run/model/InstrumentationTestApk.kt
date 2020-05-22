package ftl.run.model

import ftl.util.FileReference

data class InstrumentationTestApk(
    val app: FileReference = FileReference(),
    val test: FileReference = FileReference()
)
