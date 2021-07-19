package flank.corellium.cli

import flank.corellium.cli.RunTestCorelliumAndroidCommand.Companion.resolve
import flank.exection.parallel.ParallelState
import flank.exection.parallel.validate
import org.junit.Test

class RunTestCorelliumAndroidCommandTest {

    @Test
    fun validate() {
        val initial: ParallelState = mapOf(
            RunTestCorelliumAndroidCommand to RunTestCorelliumAndroidCommand()
        )

        resolve.validate(initial)
    }
}
