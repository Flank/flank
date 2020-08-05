package flank.scripts.release.updatebugsnag

import flank.scripts.FuelTestRunner
import org.junit.Rule
import org.junit.Test
import org.junit.contrib.java.lang.system.ExpectedSystemExit
import org.junit.runner.RunWith

@RunWith(FuelTestRunner::class)
class UpdateBugSnagCommandTest {

    @Rule
    @JvmField
    val systemExit = ExpectedSystemExit.none()!!

    @Test
    fun `Should return with exit code 0 when success`() {
        // expect
        systemExit.expectSystemExitWithStatus(0)

        // when
        UpdateBugSnagCommand().main(arrayOf("--bugsnag-api-key=success", "--app-version=1"))
    }

    @Test
    fun `Should return with exit code 1 when failure`() {
        // expect
        systemExit.expectSystemExitWithStatus(1)

        // when
        UpdateBugSnagCommand().main(arrayOf("--bugsnag-api-key=failure", "--app-version=1"))
    }
}
