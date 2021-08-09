package ftl.run.platform.android

import com.google.common.annotations.VisibleForTesting
import ftl.args.AndroidArgs
import ftl.args.isGameLoop
import ftl.args.isSanityRobo
import ftl.run.exception.FlankGeneralError
import ftl.run.model.AndroidTestContext
import ftl.run.model.GameLoopContext
import ftl.run.model.InstrumentationTestContext
import ftl.run.model.RoboTestContext
import ftl.run.model.SanityRoboTestContext
import ftl.util.asFileReference

@VisibleForTesting
internal fun AndroidArgs.resolveApks(): List<AndroidTestContext> = listOfNotNull(
    mainApkContext(),
    *additionalApksContexts()
)

private fun AndroidArgs.mainApkContext() = appApk?.let { appApk ->
    when {
        testApk != null -> InstrumentationTestContext(
            app = appApk.asFileReference(),
            test = testApk.asFileReference(),
            environmentVariables = emptyMap(),
            testTargetsForShard = testTargetsForShard,
            args = this
        )
        roboScript != null -> RoboTestContext(
            app = appApk.asFileReference(),
            roboScript = roboScript.asFileReference(),
            this
        )
        isSanityRobo -> SanityRoboTestContext(app = appApk.asFileReference(), this)
        isGameLoop -> GameLoopContext(appApk.asFileReference(), scenarioLabels, scenarioNumbers, this)
        else -> null
    }
}

private fun AndroidArgs.additionalApksContexts() = additionalAppTestApks.map {
    val appApk = (it.app ?: appApk)
        ?: throw FlankGeneralError("Cannot create app-test apks pair for instrumentation tests, missing app apk for test ${it.test}")
    val envs = it.environmentVariables.takeIf { envs -> envs.isNotEmpty() } ?: environmentVariables
    InstrumentationTestContext(
        app = appApk.asFileReference(),
        test = it.test.asFileReference(),
        environmentVariables = envs,
        testTargetsForShard = testTargetsForShard,
        args = copy(
            commonArgs = commonArgs.copy(
                maxTestShards = it.maxTestShards ?: maxTestShards,
                devices = it.devices ?: devices,
                clientDetails = it.clientDetails ?: clientDetails
            ),
            testApk = it.test,
            testTargets = it.testTargets ?: testTargets,
            parameterizedTests = it.parameterizedTests ?: parameterizedTests,
            environmentVariables = envs
        )
    )
}.toTypedArray()
