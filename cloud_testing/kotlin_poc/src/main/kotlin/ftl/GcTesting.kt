package ftl

import com.google.testing.Testing
import ftl.Constants.JSON_FACTORY
import ftl.Constants.applicationName
import ftl.Constants.httpTransport
import ftl.GlobalConfig.credential

object GcTesting {

    private var testing: Testing? = null

    init {
        testing = Testing.Builder(httpTransport, JSON_FACTORY, credential)
                .setApplicationName(applicationName)
                .build()
    }

    fun get(): Testing? {
        return testing
    }
}
