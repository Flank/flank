package flank.scripts.ops.dependencies

import flank.scripts.utils.toObject
import java.io.File

fun File.outDatedDependencies() = readText().toObject<DependenciesResultCheck>().outdated.dependencies
