package ftl.domain

import flank.common.logLn
import ftl.api.fetchDeviceModelIos
import ftl.args.IosArgs
import ftl.environment.ios.getDescription
import ftl.run.exception.FlankConfigurationError
import java.nio.file.Paths

interface DescribeIosModels {
    val modelId: String
    val configPath: String
}

operator fun DescribeIosModels.invoke() {
    if (modelId.isBlank()) throw FlankConfigurationError("Argument MODEL_ID must be specified.")
    // TODO move getDescription() and printing presentation layer during refactor of presentation after #1728
    logLn(fetchDeviceModelIos(IosArgs.loadOrDefault(Paths.get(configPath)).project).getDescription(modelId))
}
