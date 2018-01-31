package ftl.config

import org.junit.Assert.assertEquals
import org.junit.Test
import java.io.File

class YamlConfigTest {

    private fun assert(actual: Any, expected: Any) {
        assertEquals(expected, actual)
    }

    @Test
    fun addReply() {
        val path = File("./flank.yml").canonicalFile
        assert(path.exists())

        val config = YamlConfig.load(path)

        assert(config.appApk, "../../test_app/apks/app-debug.apk")
        assert(config.testApk, "../../test_app/apks/app-debug-androidTest.apk")
        assert(config.rootGcsBucket, "tmp_bucket_2")

        assert(config.useOrchestrator, true)
        assert(config.disablePerformanceMetrics, true)
        assert(config.disableVideoRecording, false)
        assert(config.testTimeoutMinutes, 60L)

        assert(config.shardCount, 1)
        assert(config.waitForResults, true)
        assert(config.testMethods, listOf("com.example.app.ExampleUiTest#testPasses"))
    }
}
