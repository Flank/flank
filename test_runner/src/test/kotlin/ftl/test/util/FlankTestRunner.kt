package ftl.test.util

import ftl.mock.MockServer
import org.junit.runner.Description
import org.junit.runner.notification.RunListener
import org.junit.runner.notification.RunNotifier
import org.junit.runners.BlockJUnit4ClassRunner

class FlankTestRunner(klass: Class<*>) : BlockJUnit4ClassRunner(klass) {

    companion object {
        init {
            println("FlankTestRunner init\n")

            MockServer.start()
        }
    }

    override fun run(notifier: RunNotifier) {
        val listener = object : RunListener() {
            override fun testFinished(description: Description?) {
                LocalGcs.clear()
                super.testFinished(description)
            }
        }
        notifier.addListener(listener)
        super.run(notifier)
    }
}
