package flank.common

import com.google.common.truth.Truth
import org.junit.After
import org.junit.Rule
import org.junit.Test
import org.junit.contrib.java.lang.system.SystemOutRule

class OutputLoggerTest {

    @Rule
    @JvmField
    val systemOutRule: SystemOutRule = SystemOutRule()
        .enableLog().muteForSuccessfulTests()

    private val simpleMessage = "print for simple"
    private val detailedMessage = "print for detailed"

    @After
    fun afterTest() {
        setLogLevel(OutputLogLevel.DETAILED)
    }

    @Test
    fun `should print messages from all output levels if log level set to detailed`() {
        setLogLevel(OutputLogLevel.DETAILED)

        logLn(simpleMessage)
        logLn(detailedMessage, OutputLogLevel.DETAILED)

        Truth.assertThat(systemOutRule.log.normalizeLineEnding()).contains(simpleMessage)
        Truth.assertThat(systemOutRule.log.normalizeLineEnding()).contains(detailedMessage)
    }

    @Test
    fun `should print messages only simple output level if log level not set`() {
        setLogLevel(OutputLogLevel.SIMPLE)

        logLn(simpleMessage)
        logLn(detailedMessage, OutputLogLevel.DETAILED)

        Truth.assertThat(systemOutRule.log.normalizeLineEnding()).contains(simpleMessage)
        Truth.assertThat(systemOutRule.log.normalizeLineEnding()).doesNotContain(detailedMessage)
    }
}
