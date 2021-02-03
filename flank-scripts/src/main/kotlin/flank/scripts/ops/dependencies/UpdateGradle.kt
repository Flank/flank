package flank.scripts.ops.dependencies

import java.io.File
import java.nio.file.Files
import java.nio.file.Paths

internal fun File.updateGradle(gradleWrapperPropertiesPath: String = "") {
    gradleDependency()
        .takeIf { it.needsUpdate() }
        ?.let { gradleDependency -> updateGradleWrapper(gradleDependency, gradleWrapperPropertiesPath) }
}

private fun updateGradleWrapper(gradleDependency: GradleDependency, gradleWrapperPropertiesPath: String) {
    findAllGradleWrapperPropertiesFiles(gradleWrapperPropertiesPath)
        .forEach {
            val from = gradleDependency.running.version
            val to = maxOf(gradleDependency.releaseCandidate.version, gradleDependency.current.version)
            println("Update gradle wrapper $from to $to in file ${it.path}")
            it.updateGradleWrapperPropertiesFile(from.toString(), to.toString())
        }
}

private fun findAllGradleWrapperPropertiesFiles(gradleWrapperPropertiesPath: String) =
    Files.walk(Paths.get(gradleWrapperPropertiesPath))
        .filter { it.fileName.toString() == GRADLE_WRAPPER_PROPERTIES_FILE }
        .map { it.toFile() }

private fun File.updateGradleWrapperPropertiesFile(from: String, to: String) = writeText(readText().replace(from, to))

private const val GRADLE_WRAPPER_PROPERTIES_FILE = "gradle-wrapper.properties"
