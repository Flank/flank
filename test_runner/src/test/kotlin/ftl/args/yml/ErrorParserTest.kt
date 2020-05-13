package ftl.args.yml

import ftl.args.AndroidArgs
import ftl.args.yml.errors.ConfigurationErrorMessageBuilder
import ftl.test.util.TestHelper
import ftl.test.util.TestHelper.getThrowable
import ftl.util.FlankConfigurationException
import org.junit.Assert
import org.junit.Test

class ErrorParserTest {
    private val yamlWithoutDeviceVersion =
        TestHelper.getPath("src/test/kotlin/ftl/args/yml/test_error_yaml_cases/flank-no-device-version.yml")
    private val yamlNoModelName =
        TestHelper.getPath("src/test/kotlin/ftl/args/yml/test_error_yaml_cases/flank-no-model-name.yml")
    private val yamlNoModelNode =
        TestHelper.getPath("src/test/kotlin/ftl/args/yml/test_error_yaml_cases/flank-no-model-node.yml")

    @Test
    fun `parse json mapping error`() {
        val instantionError =
            "Instantiation of [simple type, class ftl.config.Device] value failed for JSON property version due to missing (therefore NULL) value for creator parameter version which is a non-nullable type\n" +
                    " at [Source: (StringReader); line: 23, column: 3] (through reference chain: ftl.args.yml.AndroidGcloudYml[\"gcloud\"]->ftl.args.yml.AndroidGcloudYmlParams[\"device\"]->java.util.ArrayList[4]->ftl.config.Device[\"version\"])"

        val expected = """
Error on parse config: gcloud->device[4]->version
Missing element or value for: 'version'
At line: 23, column: 3
""".trimIndent()
        val buildErrorMessage = ConfigurationErrorMessageBuilder()
        Assert.assertEquals(expected, buildErrorMessage(instantionError))
    }

    @Test
    fun `return exception with inner message on parse error`() {
        val instantionError =
            "Instantiation oflParams[\"device\"]->java.util.A"
        val expected = "Parse message error: Instantiation oflParams[\"device\"]->java.util.A".trimIndent()
        val buildErrorMessage = ConfigurationErrorMessageBuilder()

        Assert.assertEquals(expected, buildErrorMessage(instantionError))
    }

    @Test(expected = FlankConfigurationException::class)
    fun `should throw FlankConfigException without device version`() {
        AndroidArgs.load(yamlWithoutDeviceVersion)
    }

    @Test
    fun `without model name should have message`() {
        val actualMessage = getThrowable { AndroidArgs.load(yamlNoModelName) }.message
        val exceptedMessage = """
            Error on parse config: gcloud->device[0]->model
            Missing element or value for: 'model'
            At line: 8, column: 1
            Error node: {
              "model" : null,
              "version" : "test"
            }
        """.trimIndent()
        Assert.assertEquals(exceptedMessage, actualMessage)
    }

    @Test
    fun `without model node should have message`() {
        val actualMessage = getThrowable { AndroidArgs.load(yamlNoModelNode) }.message
        val exceptedMessage = """
Error on parse config: gcloud->device
At line: 6, column: 5
Error node: {
  "app" : "./src/test/kotlin/ftl/fixtures/tmp/apk/app-debug.apk",
  "test" : "./src/test/kotlin/ftl/fixtures/tmp/apk/app-single-success-debug-androidTest.apk",
  "device" : {
    "version" : "test"
  }
}
        """.trimIndent()
        Assert.assertEquals(exceptedMessage, actualMessage)
    }

    @Test(expected = FlankConfigurationException::class)
    fun `should throw FlankConfigException without model name`() {
        AndroidArgs.load(yamlNoModelName)
    }

    @Test(expected = FlankConfigurationException::class)
    fun `should throw FlankConfigException without model node`() {
        AndroidArgs.load(yamlNoModelNode)
    }
}
