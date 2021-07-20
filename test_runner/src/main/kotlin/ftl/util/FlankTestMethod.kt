package ftl.util

import ftl.run.platform.android.SdkSuppressLevels

data class FlankTestMethod(
    val testName: String,
    val ignored: Boolean = false,
    val isParameterizedClass: Boolean = false,
    val sdkSuppressLevels: SdkSuppressLevels? = null
)
