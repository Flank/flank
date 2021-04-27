package ftl.client.google

import com.google.testing.model.AndroidVersion
import com.google.testing.model.IosVersion
import ftl.gc.GcTesting
import ftl.http.executeWithRetry

fun androidOsVersions(projectId: String): List<AndroidVersion> =
    GcTesting.get.testEnvironmentCatalog()
        .get("android")
        .setProjectId(projectId)
        .executeWithRetry()
        .androidDeviceCatalog
        .versions
        .orEmpty()

fun iosOsVersions(projectId: String): List<IosVersion> =
    GcTesting.get.testEnvironmentCatalog()
        .get("ios")
        .setProjectId(projectId)
        .executeWithRetry()
        .iosDeviceCatalog
        .versions
        .orEmpty()
