package flank.junit.mapper

import com.fasterxml.jackson.module.kotlin.readValue
import flank.junit.JUnit
import java.io.BufferedReader
import java.io.Reader

/**
 * Parse [JUnit.Report] from reader.
 *
 * @receiver Reader of XML JUnit report
 * @return Parsed structural representation of XML JUnit report
 */
internal fun Reader.parseJUnitReport(): JUnit.Report =
    buffered().run {
        readEmptyTestSuites()
            ?: xmlMapper.readValue(this)
            ?: throw IllegalArgumentException("cannot parse JUnitReport from: $this")
    }

/**
 * For <testsuites/> objectMapper is returning null.
 * This is helper method for fixing this behaviour.
 */
private fun BufferedReader.readEmptyTestSuites(): JUnit.Report? =
    JUnit.Report().takeIf {
        mark(DEFAULT_BUFFER_SIZE)
        val string = String(CharArray(DEFAULT_BUFFER_SIZE).let { buffer -> buffer.copyOf(read(buffer)) })
        reset()
        string.lines()
            .filter { it.isNotBlank() }
            .run { size < 3 && last() == "<testsuites/>" }
    }
