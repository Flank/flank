import com.google.api.services.toolresults.ToolResults

import Config.credential
import Constants.httpTransport
import Constants.JSON_FACTORY
import Constants.applicationName

object GcToolResults {

    private var toolresults: ToolResults? = null

    init {
        toolresults = ToolResults.Builder(httpTransport!!, JSON_FACTORY, credential)
                .setApplicationName(applicationName)
                .build()
    }

    fun get(): ToolResults? {
        return toolresults
    }
}
