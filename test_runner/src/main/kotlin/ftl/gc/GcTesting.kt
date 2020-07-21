package ftl.gc

import com.google.api.services.testing.Testing
import ftl.config.FtlConstants
import ftl.config.FtlConstants.JSON_FACTORY
import ftl.config.FtlConstants.applicationName
import ftl.config.FtlConstants.httpCredential
import ftl.config.FtlConstants.httpTransport
import ftl.http.executeWithRetry

object GcTesting {

    val get: Testing by lazy {
        val builder = Testing.Builder(httpTransport, JSON_FACTORY, httpCredential)
            .setApplicationName(applicationName)

        if (FtlConstants.useMock) builder.rootUrl = FtlConstants.localhost

        builder.build()
    }

    fun networkConfiguration() = get.testEnvironmentCatalog()
        .get("NETWORK_CONFIGURATION")
        .executeWithRetry()
        ?.networkConfigurationCatalog
        ?.configurations
        .orEmpty()
}
