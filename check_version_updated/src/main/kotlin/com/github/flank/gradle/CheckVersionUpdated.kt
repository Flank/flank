package com.github.flank.gradle

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.get
import java.io.ByteArrayOutputStream

class CheckVersionUpdated : Plugin<Project> {

    private val taskName = "checkIfVersionUpdated"

    override fun apply(project: Project) {

        project.tasks.register(taskName, CheckVersionUpdatedTask::class.java)

        // project.afterEvaluate {
        //     project.tasks["lintKotlin"].dependsOn(taskName)
        // }
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
        val committedResultsStream = execAndGetStdout("git", "diff", "origin/master", "--", "build.gradle.kts")
            .split("\n")
        return (committedResultsStream + localResultsStream)
            .filter { it.startsWith("-version = ") || it.startsWith("+version = ") }
            .isNotEmpty()
    }

}
