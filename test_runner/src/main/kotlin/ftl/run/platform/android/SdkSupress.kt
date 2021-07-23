package ftl.run.platform.android

import com.linkedin.dex.parser.DecodedValue
import com.linkedin.dex.parser.TestAnnotation
import com.linkedin.dex.parser.TestMethod

data class SdkSuppressLevels(
    val minSdkVersion: Int?,
    val maxSdkVersion: Int?,
)

val ALL = SdkSuppressLevels(null, null)

fun TestMethod.isSdkSuppressTest(): Boolean = annotations.sdkSuppressLevels().isNotEmpty()

fun TestMethod.sdkSuppressLevels(): SdkSuppressLevels = annotations.sdkSuppressLevels()
    .map { it.values }
    .firstOrNull { it.keys.any { name -> name == "minSdkVersion" || name == "maxSdkVersion" } }
    ?.let {
        SdkSuppressLevels(
            minSdkVersion = it["minSdkVersion"].asIntOrNull(),
            maxSdkVersion = it["maxSdkVersion"].asIntOrNull()
        )
    }
    ?: ALL

private fun List<TestAnnotation>.sdkSuppressLevels(): List<TestAnnotation> =
    filter { it.name in sdkSuppressAnnotationsNames }

private fun DecodedValue?.asIntOrNull(): Int? = (this as? DecodedValue.DecodedInt)?.value

private val sdkSuppressAnnotationsNames = arrayOf(
    "androidx.test.filters.SdkSuppress",
    "android.support.test.filters.SdkSuppress"
)
