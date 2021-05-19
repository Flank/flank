import org.gradle.internal.impldep.org.junit.Test

class MavenVersionCheckTest {

    @Test
    fun `Should properly validate maven version for snapshots`() {
        assertValidVersion("master-SNAPSHOT")
        assertValidVersion("local-SNAPSHOT")
    }

    @Test
    fun `Should properly validate maven version for not snapshots`() {
        assertValidVersion("21.02.2")
        assertValidVersion("22.01.1")
        assertValidVersion("23.11.11")
    }

    @Test
    fun `Should throw exception for not proper maven version`() {
        assert(runCatching { assertValidVersion("master_SNAPSHOT") }.isFailure)
        assert(runCatching { assertValidVersion("local_version") }.isFailure)
        assert(runCatching { assertValidVersion("version") }.isFailure)
        assert(runCatching { assertValidVersion("test") }.isFailure)
        assert(runCatching { assertValidVersion("snapshot") }.isFailure)
        assert(runCatching { assertValidVersion("2113") }.isFailure)
        assert(runCatching { assertValidVersion("2020.12.21") }.isFailure)
        assert(runCatching { assertValidVersion("21.2.1") }.isFailure)
        assert(runCatching { assertValidVersion("v21.02.01") }.isFailure)
    }
}
