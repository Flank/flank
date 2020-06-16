package ftl.run.platform.android

import ftl.args.AndroidArgs
import ftl.run.model.InstrumentationTestContext
import ftl.test.util.TestHelper
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Test

class IgnoreTestsAnnotationTest {
    private val singleSuccessYaml = TestHelper.getPath("src/test/kotlin/ftl/fixtures/test_app_cases/flank-single-success.yml")

    @Test
    fun `InstrumentationContext with @Ignore annotation should have items in ignoredTestCases`() {

        val testContext = runBlocking {
            AndroidArgs.load(singleSuccessYaml).createAndroidTestContexts()
        }.single() as InstrumentationTestContext

        Assert.assertEquals(testContext.ignoredTestCases.count(), 2)
    }

    @Test
    fun `InstrumentationContext with @Ignore annotation should't have ignoredTestCases in shards`() {

        val testContext = runBlocking {
            AndroidArgs.load(singleSuccessYaml).createAndroidTestContexts()
        }.single() as InstrumentationTestContext

        Assert.assertFalse(testContext.shards.flatten().any { it.contains("#ignoredTest") })
    }
}
