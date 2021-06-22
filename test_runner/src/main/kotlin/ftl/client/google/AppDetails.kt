package ftl.client.google

import com.google.testing.model.FileReference

fun getAndroidAppDetails(gcsAppPath: String): String =
    GcTesting.get.ApplicationDetailService().getApkDetails(FileReference().apply { gcsPath = gcsAppPath })
        .execute()?.apkDetail?.apkManifest?.packageName?.toString().orEmpty()

fun getIosAppDetails(gcsAppPath: String): String = GcTesting.get.applicationName
