package flank.scripts.dependencies.update

import flank.scripts.utils.toObject
import java.io.File

fun File.outDatedDependencies() = readText().toObject(DependenciesResultCheck.serializer()).outdated.dependencies

fun File.gradleDependency() = readText().toObject(DependenciesResultCheck.serializer()).gradle
