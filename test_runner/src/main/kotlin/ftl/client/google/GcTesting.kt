package ftl.client.google

import com.google.testing.Testing
import ftl.config.FtlConstants
import ftl.config.FtlConstants.JSON_FACTORY
import ftl.config.FtlConstants.applicationName
import ftl.config.FtlConstants.httpTransport
import ftl.util.readRevision
import ftl.util.readVersion

object GcTesting {
    val get: Testing by lazy {
        val builder = Testing.Builder(httpTransport, JSON_FACTORY, httpCredential)
            .setApplicationName("$applicationName, version: ${readVersion()}, revision: ${readRevision()}")

        if (FtlConstants.useMock) builder.rootUrl = FtlConstants.localhost

        builder.build()
    }
}
