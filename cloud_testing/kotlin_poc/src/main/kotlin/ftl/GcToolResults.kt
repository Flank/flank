package ftl

import com.google.api.services.toolresults.ToolResults
import ftl.Constants.JSON_FACTORY
import ftl.Constants.applicationName
import ftl.Constants.httpTransport
import ftl.GlobalConfig.credential

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
