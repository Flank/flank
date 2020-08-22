package ftl.run.platform.android

import com.google.common.annotations.VisibleForTesting
import ftl.args.AndroidArgs
import ftl.run.model.AndroidTestContext
import ftl.run.model.InstrumentationTestContext
import ftl.run.model.RoboTestContext
import ftl.run.exception.FlankGeneralError
import ftl.util.asFileReference

@VisibleForTesting
internal fun AndroidArgs.resolveApks(): List<AndroidTestContext> = listOfNotNull(
    appApk?.let { appApk ->
        testApk?.let { testApk ->
            InstrumentationTestContext(
                app = appApk.asFileReference(),
                test = testApk.asFileReference()
            )
        } ?: roboScript?.let { roboScript ->
            RoboTestContext(
                app = appApk.asFileReference(),
                roboScript = roboScript.asFileReference()
            )
        }
    }
).plus(
    elements = additionalAppTestApks.map {
        InstrumentationTestContext(
            app = (it.app ?: appApk)
                ?.asFileReference()
                ?: throw FlankGeneralError("Cannot create app-test apks pair for instrumentation tests, missing app apk for test ${it.test}"),
            test = it.test.asFileReference()
        )
    }
)
