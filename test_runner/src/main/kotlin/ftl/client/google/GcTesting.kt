package ftl.client.google

import com.google.testing.Testing
import ftl.config.FtlConstants
import ftl.config.FtlConstants.JSON_FACTORY
import ftl.config.FtlConstants.applicationName
import ftl.config.FtlConstants.httpTransport

object GcTesting {
    val get: Testing by lazy {
        val builder = Testing.Builder(httpTransport, JSON_FACTORY, httpCredential)
            .setApplicationName(applicationName)

        if (FtlConstants.useMock) builder.rootUrl = FtlConstants.localhost

        builder.build()
    }
}
