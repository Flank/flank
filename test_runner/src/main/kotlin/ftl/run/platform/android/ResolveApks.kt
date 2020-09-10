package ftl.run.platform.android

import com.google.common.annotations.VisibleForTesting
import ftl.args.AndroidArgs
import ftl.args.isSanityRobo

import ftl.run.model.AndroidTestContext
import ftl.run.model.InstrumentationTestContext
import ftl.run.model.RoboTestContext
import ftl.run.exception.FlankGeneralError
import ftl.run.model.SanityRoboTestContext
import ftl.util.asFileReference

@VisibleForTesting
internal fun AndroidArgs.resolveApks(): List<AndroidTestContext> = listOfNotNull(
    mainApkContext(),
    *additionalApksContexts()
)

private fun AndroidArgs.mainApkContext() = appApk?.let { appApk ->
    when {
        testApk != null -> InstrumentationTestContext(app = appApk.asFileReference(), test = testApk.asFileReference())
        roboScript != null -> RoboTestContext(app = appApk.asFileReference(), roboScript = roboScript.asFileReference())
        isSanityRobo -> SanityRoboTestContext(app = appApk.asFileReference())
        else -> null
    }
}

private fun AndroidArgs.additionalApksContexts() = additionalAppTestApks.map {
    InstrumentationTestContext(
        app = (it.app ?: appApk)
            ?.asFileReference()
            ?: throw FlankGeneralError("Cannot create app-test apks pair for instrumentation tests, missing app apk for test ${it.test}"),
        test = it.test.asFileReference()
    )
}.toTypedArray()
