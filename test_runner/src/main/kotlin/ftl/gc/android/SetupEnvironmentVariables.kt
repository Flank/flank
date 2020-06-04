package ftl.gc.android

import com.google.api.services.testing.model.EnvironmentVariable
import com.google.api.services.testing.model.TestSetup
import com.google.common.annotations.VisibleForTesting
import ftl.args.AndroidArgs
import ftl.run.platform.android.AndroidTestConfig

@VisibleForTesting
internal fun TestSetup.setEnvironmentVariables(args: AndroidArgs, testConfig: AndroidTestConfig) = this.apply {
    environmentVariables = when (testConfig) {
        is AndroidTestConfig.Instrumentation -> args.environmentVariables.map { it.toEnvironmentVariable() }
        is AndroidTestConfig.Robo -> emptyList()
    }
}


private fun Map.Entry<String, String>.toEnvironmentVariable() = EnvironmentVariable().apply {
    key = this@toEnvironmentVariable.key
    value = this@toEnvironmentVariable.value
}
