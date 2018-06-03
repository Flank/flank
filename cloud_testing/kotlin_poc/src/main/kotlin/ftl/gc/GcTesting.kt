package ftl.gc

import com.google.api.client.googleapis.services.AbstractGoogleClientRequest
import com.google.api.client.googleapis.services.GoogleClientRequestInitializer
import com.google.api.services.testing.Testing
import ftl.config.FtlConstants
import ftl.config.FtlConstants.JSON_FACTORY
import ftl.config.FtlConstants.applicationName
import ftl.config.FtlConstants.credential
import ftl.config.FtlConstants.httpTransport

// https://github.com/bootstraponline/gcloud_cli/blob/40521a6e297830b9f652a9ab4d8002e309b4353a/google-cloud-sdk/lib/googlecloudsdk/core/credentials/http.py#L82
private class BillingQuotaProject internal constructor() : GoogleClientRequestInitializer {
    override fun initialize(request: AbstractGoogleClientRequest<*>) {
        // TODO: read project id from config
        request.requestHeaders.set("X-Goog-User-Project", "delta-essence-114723")
    }
}

object GcTesting {

    val get: Testing by lazy {
        val builder = Testing.Builder(httpTransport, JSON_FACTORY, credential)
                .setApplicationName(applicationName)
                .setGoogleClientRequestInitializer(BillingQuotaProject())

        if (FtlConstants.useMock) builder.rootUrl = FtlConstants.localhost

        builder.build()
    }
}
