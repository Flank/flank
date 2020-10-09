package flank.scripts.dependencies.update

import org.junit.Test

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Assert.assertFalse

class DependencyExtensionsTest {

    @Test
    fun `should return group with name`() {
        // given
        val dependency = Dependency(
            "group",
            "1.0",
            "name"
        )

        // when
        val actual = dependency.groupWithName

        // then
        assertEquals("${dependency.group}:${dependency.name}:", actual)
    }

    @Test
    fun `should get version to update if release available`() {
        val dependency = Dependency(
            "group",
            "1.0",
            "name",
            AvailableVersion(
                "1.1", null, null
            )
        )

        // when
        val actual = dependency.versionToUpdate

        // then
        assertEquals(dependency.availableVersion?.release, actual)
    }

    @Test
    fun `should get version to update if milestone available`() {
        val dependency = Dependency(
            "group",
            "1.0",
            "name",
            AvailableVersion(null, "1.1", null)
        )

        // when
        val actual = dependency.versionToUpdate

        // then
        assertEquals(dependency.availableVersion?.milestone, actual)
    }

    @Test
    fun `should get version to update if integration available`() {
        val dependency = Dependency(
            "group",
            "1.0",
            "name",
            AvailableVersion(null, null, "1.1")
        )

        // when
        val actual = dependency.versionToUpdate

        // then
        assertEquals(dependency.availableVersion?.integration, actual)
    }

    @Test
    fun `should get current version to update if no update`() {
        val dependency = Dependency(
            "group",
            "1.0",
            "name",
            null
        )

        // when
        val actual = dependency.versionToUpdate

        // then
        assertEquals(dependency.version, actual)
    }

    @Test
    fun `should properly check if gradle needs update`() {
        // given
        val gradleWhichNeedsUpdate = GradleDependency(
            current = GradleVersion("1.1", "test", false, false),
            nightly = GradleVersion("1.3", "test", false, false),
            releaseCandidate = GradleVersion("1.2rc", "test", false, false),
            running = GradleVersion("1", "test", false, false),
        )
        val gradleWhichNeedsUpdateRc = GradleDependency(
            current = GradleVersion("1.1", "test", false, false),
            nightly = GradleVersion("1.3", "test", false, false),
            releaseCandidate = GradleVersion("1.2rc", "test", false, false),
            running = GradleVersion("1.1", "test", false, false),
        )
        val gradleWhichDoesNotNeedUpdate = GradleDependency(
            current = GradleVersion("1.1", "test", false, false),
            nightly = GradleVersion("1.3", "test", false, false),
            releaseCandidate = GradleVersion("1.1", "test", false, false),
            running = GradleVersion("1.1", "test", false, false),
        )


            // when - then
        assertTrue(gradleWhichNeedsUpdate.needsUpdate())
        assertTrue(gradleWhichNeedsUpdateRc.needsUpdate())
        assertFalse(gradleWhichDoesNotNeedUpdate.needsUpdate())
    }
}
