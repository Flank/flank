import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.client.http.HttpTransport
import com.google.api.client.json.JsonFactory
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.cloud.ServiceOptions

import Utils.fatalError

object Constants {

    val applicationName = "Flank"
    val projectId = ServiceOptions.getDefaultProjectId()
    val GCS_PREFIX = "gs://"
    val JSON_FACTORY: JsonFactory = JacksonFactory.getDefaultInstance()

    var httpTransport: HttpTransport? = null

    init {
        try {
            httpTransport = GoogleNetHttpTransport.newTrustedTransport()
        } catch (e: Exception) {
            fatalError(e)
        }

    }
}
