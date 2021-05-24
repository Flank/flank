package com.github.flank.gradle

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.PublishException
import org.gradle.api.publish.maven.tasks.PublishToMavenRepository
import org.gradle.internal.impldep.org.jetbrains.annotations.VisibleForTesting
import org.gradle.kotlin.dsl.withType

class MavenVersionCheck : Plugin<Project> {
    override fun apply(project: Project) {
        project.tasks.withType<PublishToMavenRepository>().configureEach {
            doFirst {
                assertValidVersion(publication.version)
            }
        }
    }

    @VisibleForTesting
    internal fun assertValidVersion(version: String) {
        val validSnapshots = arrayOf("master-SNAPSHOT", "local-SNAPSHOT")
        val versionRegex = "2\\d\\.\\d{2}\\.\\d{1,2}".toRegex()
        if (version !in validSnapshots && !versionRegex.matches(version)) {
            throw PublishException("Maven version is not valid!")
        }
    }
}
