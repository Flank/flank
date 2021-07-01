package com.github.flank.wrapper.internal

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class FlankExecutorTest {

    @Test
    fun `Should properly pass args to Flank command`() {
        // given
        val args = arrayOf("firebase", "test", "android", "run", "--project=test", "--dump-shards")
        val expected = "java -jar $flankRunnerPath firebase test android run --project=test --dump-shards"

        // when
        val actual = buildRunCommand(args)

        // then
        assertThat(actual.joinToString(" ")).isEqualTo(expected)
    }
}
