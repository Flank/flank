package ftl.gc.android

import com.google.testing.model.EnvironmentVariable
import com.google.testing.model.TestSetup
import com.google.common.annotations.VisibleForTesting
import ftl.args.AndroidArgs
import ftl.run.platform.android.AndroidTestConfig

@VisibleForTesting
internal fun TestSetup.setEnvironmentVariables(args: AndroidArgs, testConfig: AndroidTestConfig) = apply {
    environmentVariables = when (testConfig) {
        is AndroidTestConfig.Instrumentation ->
            testConfig.environmentVariables.map { it.toEnvironmentVariable() } + args.environmentVariables.map { it.toEnvironmentVariable() }
        is AndroidTestConfig.Robo -> emptyList()
        is AndroidTestConfig.GameLoop -> emptyList()
    }.distinctBy { it.key }
}

private fun Map.Entry<String, String>.toEnvironmentVariable() = EnvironmentVariable().apply {
    key = this@toEnvironmentVariable.key
    value = this@toEnvironmentVariable.value
}
