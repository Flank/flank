package ftl.gc.android

import com.google.common.annotations.VisibleForTesting
import com.google.testing.model.EnvironmentVariable
import com.google.testing.model.TestSetup
import ftl.api.TestMatrixAndroid

@VisibleForTesting
internal fun TestSetup.setEnvironmentVariables(args: Map<String, String>, testConfig: TestMatrixAndroid.Type) = apply {
    environmentVariables = when (testConfig) {
        is TestMatrixAndroid.Type.Instrumentation ->
            testConfig.environmentVariables.map { it.toEnvironmentVariable() } + args.map { it.toEnvironmentVariable() }
        is TestMatrixAndroid.Type.Robo -> emptyList()
        is TestMatrixAndroid.Type.GameLoop -> emptyList()
    }.distinctBy { it.key }
}

private fun Map.Entry<String, String>.toEnvironmentVariable() = EnvironmentVariable().apply {
    key = this@toEnvironmentVariable.key
    value = this@toEnvironmentVariable.value
}
