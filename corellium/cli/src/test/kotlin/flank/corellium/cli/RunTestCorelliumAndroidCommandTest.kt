package flank.corellium.cli

import flank.corellium.cli.TestAndroidCommand.Companion.resolve
import flank.exection.parallel.ParallelState
import flank.exection.parallel.validate
import org.junit.Test

class RunTestCorelliumAndroidCommandTest {

    @Test
    fun validate() {
        val initial: ParallelState = mapOf(
            TestAndroidCommand to TestAndroidCommand()
        )

        resolve.validate(initial)
    }
}
