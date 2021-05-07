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
            testTargetsForShard = testTargetsForShard
        )
        roboScript != null -> RoboTestContext(app = appApk.asFileReference(), roboScript = roboScript.asFileReference())
        isSanityRobo -> SanityRoboTestContext(app = appApk.asFileReference())
        isGameLoop -> GameLoopContext(appApk.asFileReference(), scenarioLabels, scenarioNumbers)
        else -> null
    }
}

private fun AndroidArgs.additionalApksContexts() = additionalAppTestApks.map {
    val appApk = (it.app ?: appApk)
        ?: throw FlankGeneralError("Cannot create app-test apks pair for instrumentation tests, missing app apk for test ${it.test}")
    InstrumentationTestContext(
        app = appApk.asFileReference(),
        test = it.test.asFileReference(),
        environmentVariables = it.environmentVariables,
        testTargetsForShard = testTargetsForShard,
        maxTestShards = it.maxTestShards,
        clientDetails = it.clientDetails,
    )
}.toTypedArray()
