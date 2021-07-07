package ftl.client.google

import com.google.testing.Testing
import com.google.testing.TestingRequest
import com.google.testing.TestingRequestInitializer
import ftl.config.FtlConstants
import ftl.config.FtlConstants.JSON_FACTORY
import ftl.config.FtlConstants.applicationName
import ftl.config.FtlConstants.httpTransport
import ftl.util.applicationInfo

object GcTesting {
    val get: Testing by lazy {
        val builder = Testing.Builder(httpTransport, JSON_FACTORY, httpCredential)
            .setApplicationName(applicationName)

        if (FtlConstants.useMock) builder.rootUrl = FtlConstants.localhost

        builder.setTestingRequestInitializer(object : TestingRequestInitializer() {
            override fun initializeTestingRequest(request: TestingRequest<*>?) {
                super.initializeTestingRequest(request)
                val (clientDetails, clientDetailsInfo) = applicationInfo
                request?.requestHeaders?.set(clientDetails, clientDetailsInfo)
            }
        })

        builder.build()
    }
}
