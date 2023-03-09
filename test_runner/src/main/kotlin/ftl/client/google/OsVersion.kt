package ftl.client.google

import com.google.api.client.http.HttpHeaders
import com.google.testing.model.AndroidVersion
import com.google.testing.model.IosVersion
import ftl.config.FtlConstants.GCS_PROJECT_HEADER
import ftl.http.executeWithRetry

fun androidOsVersions(projectId: String): List<AndroidVersion> =
    GcTesting.get.testEnvironmentCatalog()
        .get("android")
        .setProjectId(projectId)
        .setRequestHeaders(HttpHeaders().set(GCS_PROJECT_HEADER, projectId))
        .executeWithRetry()
        .androidDeviceCatalog
        .versions
        .orEmpty()

fun iosOsVersions(projectId: String): List<IosVersion> =
    GcTesting.get.testEnvironmentCatalog()
        .get("ios")
        .setProjectId(projectId)
        .setRequestHeaders(HttpHeaders().set(GCS_PROJECT_HEADER, projectId))
        .executeWithRetry()
        .iosDeviceCatalog
        .versions
        .orEmpty()