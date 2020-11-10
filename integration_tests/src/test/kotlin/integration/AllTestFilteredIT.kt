package integration

import FlankCommand
import run
import org.junit.Test

class AllTestFilteredIT {

    @Test
    fun `filter all tests - android`() {
        val result = FlankCommand(
            flankPath = "../test_runner/build/libs/flank.jar",
            ymlPath = "./src/test/resources/all_test_filtered_android.yml",
            params = androidRunCommands
        ).run(
            workingDirectory = "./",
            testSuite = this::class.java.simpleName
        )

        assertExitCode(result, 1)

        Assert that result.output.removeUnicode() contains  {
            customConfig(
                "test" with "[0-9a-zA-Z\\/_]*.apk",
                "disable-sharding" with true,
                "test-targets" with listOf(
                    "- class non.existing.Class"
                )
            )
            output {
                noneTestRan()
                androidShards(tests = 0)
            }
            noOutcomeSummary()
        }
    }
}
