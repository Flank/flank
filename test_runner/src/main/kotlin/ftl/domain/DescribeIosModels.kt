package ftl.domain

import ftl.api.fetchDeviceModelIos
import ftl.args.IosArgs
import ftl.presentation.Output
import ftl.run.exception.FlankConfigurationError
import ftl.run.exception.FlankGeneralError
import java.nio.file.Paths

interface DescribeIosModels : Output {
    val modelId: String
    val configPath: String
}

operator fun DescribeIosModels.invoke() {
    if (modelId.isBlank()) throw FlankConfigurationError("Argument MODEL_ID must be specified.")
    fetchDeviceModelIos(IosArgs.loadOrDefault(Paths.get(configPath)).project)
        .find { it.id == modelId }
        ?.out()
        ?: throw FlankGeneralError("ERROR: '$modelId' is not a valid model")
}
