package ftl.client.google

import com.google.api.services.testing.model.AndroidVersion
import com.google.api.services.testing.model.IosVersion
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
