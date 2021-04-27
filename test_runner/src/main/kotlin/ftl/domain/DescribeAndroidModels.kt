package ftl.domain

import flank.common.logLn
import ftl.api.fetchDeviceModelAndroid
import ftl.args.AndroidArgs
import ftl.environment.android.getDescription
import ftl.run.exception.FlankConfigurationError
import java.nio.file.Paths

interface DescribeAndroidModels {
    val configPath: String
    val modelId: String
}

operator fun DescribeAndroidModels.invoke() {
    if (modelId.isBlank()) throw FlankConfigurationError("Argument MODEL_ID must be specified.")
    // TODO move getDescription() and printing presentation layer during refactor of presentation after #1728
    logLn(fetchDeviceModelAndroid(AndroidArgs.loadOrDefault(Paths.get(configPath)).project).getDescription(modelId))
}
