import com.google.testing.Testing

import Config.credential
import Constants.httpTransport
import Constants.JSON_FACTORY
import Constants.applicationName

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
