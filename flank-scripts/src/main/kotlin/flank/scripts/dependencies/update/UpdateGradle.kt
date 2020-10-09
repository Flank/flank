package flank.scripts.dependencies.update

import java.io.File
import java.nio.file.Files
import java.nio.file.Paths

fun File.updateGradle(gradleWrapperPropertiesPath: String = "") {
    gradleDependency()
        .takeIf { it.needsUpdate() }
        ?.let { gradleDependency -> updateGradleWrapper(gradleDependency, gradleWrapperPropertiesPath) }
}

private fun updateGradleWrapper(gradleDependency: GradleDependency, gradleWrapperPropertiesPath: String) {
    findAllGradleWrapperPropertiesFiles(gradleWrapperPropertiesPath)
        .forEach {
            val from = gradleDependency.running.version
            val to = if (gradleDependency.releaseCandidate.isUpdateAvailable)
                gradleDependency.releaseCandidate.version
            else
                gradleDependency.current.version
            println("Update gradle wrapper $from to $to in file ${it.path}")
            it.updateGradleWrapperPropertiesFile(from, to)
        }
}

private fun findAllGradleWrapperPropertiesFiles(gradleWrapperPropertiesPath: String) =
    Files.walk(Paths.get(gradleWrapperPropertiesPath))
        .filter { it.fileName.toString() == GRADLE_WRAPPER_PROPERTIES_FILE }
        .map { it.toFile() }

private fun File.updateGradleWrapperPropertiesFile(from: String, to: String) {
    writeText(readText().replace(from, to))
}

private const val GRADLE_WRAPPER_PROPERTIES_FILE = "gradle-wrapper.properties"
