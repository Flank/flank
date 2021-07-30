package com.github.flank.gradle

import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.Project
import org.gradle.api.tasks.TaskAction
import java.io.ByteArrayOutputStream

open class CheckVersionUpdatedTask : DefaultTask() {

    @TaskAction
    fun action() {
        project.execAndGetStdout("git", "fetch", "--no-tags")
        val changedFiles = project.execAndGetStdout("git", "diff", "origin/master", "HEAD", "--name-only").split("\n") +
            project.execAndGetStdout("git", "diff", "origin/master", "--name-only").split("\n")
        val isVersionChanged = changedFiles.any { it.startsWith(project.name) }.not() ||
            (changedFiles.contains("${project.name}/build.gradle.kts") && project.isVersionChangedInBuildGradle())

        if (isVersionChanged.not()) {
            throw GradleException(
                """
                   ${project.path} version is not updated, but files changed.
                   Please update version according to schema: <breaking change>.<feature added>.<fix/minor change>
                """.trimIndent()
            )
        }
    }

    private fun Project.execAndGetStdout(vararg args: String): String {
        val stdout = ByteArrayOutputStream()
        exec {
            commandLine(*args)
            standardOutput = stdout
            workingDir = projectDir
        }
        return stdout.toString().trimEnd()
    }

    private fun Project.isVersionChangedInBuildGradle(): Boolean {
        val localResultsStream = execAndGetStdout("git", "diff", "origin/master", "HEAD", "--", "build.gradle.kts")
            .split("\n")
        val commitedResultsStream = execAndGetStdout("git", "diff", "origin/master", "--", "build.gradle.kts")
            .split("\n")
        return (commitedResultsStream + localResultsStream)
            .any { it.startsWith("-version = ") || it.startsWith("+version = ") }
    }
}
