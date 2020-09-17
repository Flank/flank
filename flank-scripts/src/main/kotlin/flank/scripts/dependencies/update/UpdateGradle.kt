package flank.scripts.dependencies

import flank.scripts.dependencies.update.GradleDependency
import flank.scripts.dependencies.update.gradleDependency
import flank.scripts.dependencies.update.needsUpdate
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths

fun File.updateGradle() {
    gradleDependency()
        .takeIf { it.needsUpdate() }
        ?.let { gradleDependency -> updateGradleWrapper(gradleDependency) }
}

private fun updateGradleWrapper(gradleDependency: GradleDependency) {
    findAllGradleWrapperPropertiesFiles()
        .forEach {
            val from = gradleDependency.running.version
            val to = gradleDependency.current.version
            println("Update gradle wrapper $from to $to in file ${it.path}")
            it.updateGradleWrapperPropertiesFile(from, to)
        }
}

private fun findAllGradleWrapperPropertiesFiles() = Files.walk(Paths.get(""))
    .filter { it.fileName.toString() == GRADLE_WRAPPER_PROPERTIES_FILE }
    .map { it.toFile() }

private fun File.updateGradleWrapperPropertiesFile(from: String, to: String) {
    writeText(readText().replace(from, to))
}

private const val GRADLE_WRAPPER_PROPERTIES_FILE = "gradle-wrapper.properties"
