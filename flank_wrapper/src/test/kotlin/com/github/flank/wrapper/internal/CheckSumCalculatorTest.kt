package com.github.flank.wrapper.internal

import com.google.common.truth.Truth.assertThat
import org.junit.Test
import kotlin.io.path.createTempFile

class CheckSumCalculatorTest {

    @Test
    fun `should properly calculate file checksum`() {
        // given
        val testFile = createTempFile("test", "file").toFile().apply {
            writeText("sample text for test file")
        }
        val expected = "5d6789b556c38bec30c01443f7b7adbc824c8af26cea6ff6c0aa16ecd846bc54"

        // when
        val actual = testFile.sha256()

        // then
        assertThat(actual).isEqualTo(expected)
    }
}
