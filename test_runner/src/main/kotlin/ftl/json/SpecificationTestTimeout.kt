package ftl.json

import com.google.api.services.toolresults.model.Specification

fun Specification.testTimeoutSeconds() = this.getPlatformTest()?.toTest()?.getDurationSeconds()

private fun Specification.getPlatformTest() = if (this[ANDROID_TEST_KEY] != null) this[ANDROID_TEST_KEY]
else this[IOS_TEST_KEY]

@Suppress("UNCHECKED_CAST")
private fun Any.toTest() = (this as GenericTestMap)
private typealias GenericTestMap = Map<String, Map<String, Any>>

private fun GenericTestMap.getDurationSeconds() = this[TEST_TIMEOUT_KEY]?.get(SECONDS_KEY).toString().toLong()

private const val ANDROID_TEST_KEY = "androidTest"
private const val IOS_TEST_KEY = "iosTest"
private const val TEST_TIMEOUT_KEY = "testTimeout"
private const val SECONDS_KEY = "seconds"
