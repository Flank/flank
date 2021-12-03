package ftl.client.google

import com.google.testing.model.FileReference
import ftl.http.executeWithRetry

fun getAndroidAppDetails(gcsAppPath: String): String {
    // getApkDetails errors when sent non-apk files such as aab
    if (gcsAppPath.trim().lowercase().endsWith(".apk").not()) return ""

    return GcTesting.get
        .ApplicationDetailService()
        .getApkDetails(FileReference().apply { gcsPath = gcsAppPath })
        .apply { requestHeaders.set("X-Server-Timeout", 1800) } // 30 min
        .executeWithRetry()
        ?.apkDetail?.apkManifest?.packageName?.toString().orEmpty()
}
