package ftl.client.google

import com.google.testing.model.FileReference
import ftl.http.executeWithRetry

fun getAndroidAppDetails(gcsAppPath: String): String =
    GcTesting.get.ApplicationDetailService().getApkDetails(FileReference().apply { gcsPath = gcsAppPath })
        .executeWithRetry()?.apkDetail?.apkManifest?.packageName?.toString().orEmpty()
