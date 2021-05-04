package ftl.domain

import ftl.api.fetchDeviceModelAndroid
import ftl.args.AndroidArgs
import ftl.presentation.Output
import ftl.run.exception.FlankConfigurationError
import ftl.run.exception.FlankGeneralError
import java.nio.file.Paths

interface DescribeAndroidModels : Output {
    val configPath: String
    val modelId: String
}

operator fun DescribeAndroidModels.invoke() {
    if (modelId.isBlank()) throw FlankConfigurationError("Argument MODEL_ID must be specified.")
    fetchDeviceModelAndroid(AndroidArgs.loadOrDefault(Paths.get(configPath)).project)
        .find { it.id == modelId }
        ?.out()
        ?: throw FlankGeneralError("ERROR: '$modelId' is not a valid model")
}
