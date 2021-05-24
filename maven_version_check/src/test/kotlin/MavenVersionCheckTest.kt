package com.github.flank.gradle

import org.junit.Assert.assertTrue
import org.junit.Test

class MavenVersionCheckTest {

    @Test
    fun `Should properly validate maven version for snapshots`() {
        with(MavenVersionCheck()) {
            assertValidVersion("master-SNAPSHOT")
            assertValidVersion("local-SNAPSHOT")
        }
    }

    @Test
    fun `Should properly validate maven version for not snapshots`() {
        with(MavenVersionCheck()) {
            assertValidVersion("21.02.0")
            assertValidVersion("22.01.1")
            assertValidVersion("23.11.11")
        }
    }

    @Test
    fun `Should throw exception for not proper maven version`() {
        with(MavenVersionCheck()) {
            assertTrue(runCatching { assertValidVersion("master_SNAPSHOT") }.isFailure)
            assertTrue(runCatching { assertValidVersion("local_version") }.isFailure)
            assertTrue(runCatching { assertValidVersion("version") }.isFailure)
            assertTrue(runCatching { assertValidVersion("test") }.isFailure)
            assertTrue(runCatching { assertValidVersion("snapshot") }.isFailure)
            assertTrue(runCatching { assertValidVersion("2113") }.isFailure)
            assertTrue(runCatching { assertValidVersion("2020.12.21") }.isFailure)
            assertTrue(runCatching { assertValidVersion("21.2.1") }.isFailure)
            assertTrue(runCatching { assertValidVersion("v21.02.01") }.isFailure)
            assertTrue(runCatching { assertValidVersion("19.02.1") }.isFailure)
        }
    }
}
