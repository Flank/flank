package ftl.gc

import com.google.testing.Testing
import ftl.config.FtlConstants.JSON_FACTORY
import ftl.config.FtlConstants.applicationName
import ftl.config.FtlConstants.credential
import ftl.config.FtlConstants.httpTransport

object GcTesting {

    val get: Testing by lazy {
        Testing.Builder(httpTransport, JSON_FACTORY, credential)
                .setApplicationName(applicationName)
                .build()
    }
}
