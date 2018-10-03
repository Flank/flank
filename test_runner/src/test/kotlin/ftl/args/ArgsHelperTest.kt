package ftl.args

import com.google.common.truth.Truth.assertThat
import ftl.args.ArgsHelper.assertFileExists
import ftl.args.ArgsHelper.assertGcsFileExists
import ftl.args.ArgsHelper.calculateShards
import ftl.args.ArgsHelper.getGcsBucket
import ftl.args.ArgsHelper.mergeYmlMaps
import ftl.args.ArgsHelper.validateTestMethods
import ftl.args.yml.GcloudYml
import ftl.args.yml.IosGcloudYml
import ftl.test.util.FlankTestRunner
import org.junit.Rule
import org.junit.Test
import org.junit.contrib.java.lang.system.SystemErrRule
import org.junit.rules.ExpectedException
import org.junit.runner.RunWith

@RunWith(FlankTestRunner::class)
class ArgsHelperTest {

    @Rule
    @JvmField
    val exceptionRule = ExpectedException.none()!!

    @Rule
    @JvmField
    val systemErrRule = SystemErrRule().muteForSuccessfulTests()!!

    @Test
    fun mergeYmlMaps_succeeds() {
        val merged = mergeYmlMaps(GcloudYml, IosGcloudYml)
        assertThat(merged.keys.size).isEqualTo(1)
        assertThat(merged["gcloud"]?.size).isEqualTo(10)
    }

    @Test
    fun assertFileExists_succeeds() {
        assertFileExists("/tmp", "temp folder")
    }

    @Test
    fun assertFileExists_fails() {
        exceptionRule.expectMessage("'/tmp/1/2/3/fake'  doesn't exist")
        assertFileExists("/tmp/1/2/3/fake", "")
    }

    @Test
    fun assertGcsFileExists_succeeds() {
        assertGcsFileExists("gs://tmp_bucket_2/app-debug.apk")
    }

    @Test
    fun assertGcsFileExists_fails() {
        exceptionRule.expectMessage("The file at 'gs://does-not-exist' does not exist")
        assertGcsFileExists("gs://does-not-exist")
    }

    @Test(expected = IllegalArgumentException::class)
    fun assertGcsFileExists_failsOnMissingPrefix() {
        assertGcsFileExists("does-not-exist")
    }

    @Test
    fun validateTestMethods_succeeds() {
        val testTargets = listOf("a")
        val validTestMethods = listOf("a", "b", "c")
        val from = "Test APK"
        validateTestMethods(testTargets, validTestMethods, from)
    }

    @Test
    fun validateTestMethods_validationOffWhenUseMock() {
        val testTargets = listOf("d")
        val validTestMethods = listOf("a", "b", "c")
        validateTestMethods(testTargets, validTestMethods, "")
    }

    @Test
    fun validateTestMethods_validationOn() {
        exceptionRule.expectMessage(" is missing methods: [d].")
        val testTargets = listOf("d")
        val validTestMethods = listOf("a", "b", "c")
        val skipValidation = false
        validateTestMethods(testTargets, validTestMethods, "", skipValidation)
    }

    @Test
    fun validateTestMethods_validationOn_Empty() {
        exceptionRule.expectMessage("has no tests")
        val testTargets = emptyList<String>()
        val validTestMethods = emptyList<String>()
        val skipValidation = false
        validateTestMethods(testTargets, validTestMethods, "", skipValidation)
    }

    @Test
    fun calculateShards_fails_emptyShardChunks() {
        exceptionRule.expectMessage("Failed to populate test shard chunks")
        calculateShards(
            testTargets = listOf(""),
            validTestNames = listOf(""),
            testMethodsAlwaysRun = listOf(""),
            testShards = 1
        )
    }

    @Test
    fun calculateShards_succeeds() {
        calculateShards(
            testTargets = listOf("a", "b"),
            validTestNames = listOf("a", "b", "c"),
            testMethodsAlwaysRun = listOf("c"),
            testShards = -1
        )
    }

    @Test
    fun calculateShards_emptyTestTargets() {
        calculateShards(
            testTargets = listOf(),
            validTestNames = listOf("a", "b", "c"),
            testMethodsAlwaysRun = listOf("c"),
            testShards = 2
        )
    }

    @Test
    fun yamlMapper_exists() {
        assertThat(ArgsHelper.yamlMapper).isNotNull()
    }

    @Test
    fun getGcsBucket_succeeds() {
        getGcsBucket("123", "results_bucket")
    }

    @Test
    fun getDefaultProjectId_succeeds() {
        assertThat(ArgsHelper.getDefaultProjectId())
            .isEqualTo("mockProjectId")
    }
}
