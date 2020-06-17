package ftl.gc

import com.google.common.annotations.VisibleForTesting

@VisibleForTesting
internal fun generateApkFileName(actualFileName: String, counter: Int? = null) = actualFileName.run { counter?.let { replace(".apk", "_$counter.apk") } ?: this }
