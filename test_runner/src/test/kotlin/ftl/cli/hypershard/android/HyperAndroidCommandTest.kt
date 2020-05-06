package ftl.cli.hypershard.android

import ftl.util.FlankFatalError
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.contrib.java.lang.system.SystemOutRule
import java.io.File

class HyperAndroidCommandTest {

    @get:Rule
    val systemOutRule: SystemOutRule = SystemOutRule().enableLog().muteForSuccessfulTests()

    @Test(expected = FlankFatalError::class)
    fun `should throw FlankFatalError if dirs parameter was not provided`() {
        HyperAndroidCommand().run()
    }

    @Test
    fun `should create file with tests with given annotation`() {
        HyperAndroidCommand().run {
            dirs = listOf("./src/test/kotlin/ftl/cli/hypershard/android")
            annotationName = "TestAnnotation"
            run()
        }
        File("hypershard-android-results.txt").apply {
            assertTrue(readLines().containsAll(
                listOf(
                    "ftl.cli.hypershard.android.ExampleTestWithAnnotation#test1WithAnnotation",
                    "ftl.cli.hypershard.android.ExampleTestWithAnnotation#test2WithAnnotation"
                )
            ))
        }.deleteOnExit()
    }
}
