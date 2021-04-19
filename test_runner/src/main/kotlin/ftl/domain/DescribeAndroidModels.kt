package ftl.domain

import flank.common.logLn
import ftl.args.AndroidArgs
import ftl.client.google.AndroidCatalog
import ftl.run.exception.FlankConfigurationError
import java.nio.file.Paths

interface DescribeAndroidModels {
    val configPath: String
    val modelId: String
}

operator fun DescribeAndroidModels.invoke() {
    if (modelId.isBlank()) throw FlankConfigurationError("Argument MODEL_ID must be specified.")
    logLn(AndroidCatalog.describeModel(AndroidArgs.loadOrDefault(Paths.get(configPath)).project, modelId))
}
