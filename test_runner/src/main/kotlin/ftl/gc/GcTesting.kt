package ftl.gc

import com.google.api.services.testing.Testing
import ftl.config.FtlConstants
import ftl.config.FtlConstants.JSON_FACTORY
import ftl.config.FtlConstants.applicationName
import ftl.config.FtlConstants.credential
import ftl.config.FtlConstants.httpTransport

object GcTesting {

    val get: Testing by lazy {
        val builder = Testing.Builder(httpTransport, JSON_FACTORY, credential)
            .setApplicationName(applicationName)

        if (FtlConstants.useMock) builder.rootUrl = FtlConstants.localhost

        builder.build()
    }
}
