package ftl.domain

import flank.common.logLn
import ftl.args.IosArgs
import ftl.client.google.IosCatalog
import ftl.run.exception.FlankConfigurationError
import java.nio.file.Paths

interface DescribeIosModels {
    val modelId: String
    val configPath: String
}

operator fun DescribeIosModels.invoke() {
    if (modelId.isBlank()) throw FlankConfigurationError("Argument MODEL_ID must be specified.")
    logLn(IosCatalog.describeModel(IosArgs.loadOrDefault(Paths.get(configPath)).project, modelId))
}
