package com.github.flank.wrapper.internal

import com.google.common.truth.Truth.assertThat
import flank.common.dotFlank
import org.junit.Test

class FlankRunnerPathTest {

    @Test
    fun `should return correct path for flank runner`() {
        assertThat(flankRunnerPath).isEqualTo("$dotFlank/flank.jar")
    }
}
