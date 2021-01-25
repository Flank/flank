package flank.scripts.ops.dependencies

import java.io.File

fun File.updateAllDependencies(
    dependenciesFile: File,
    versionsFile: File,
    pluginsFile: File
) {
    updateDependencies(
        dependenciesFile = dependenciesFile,
        versionsFile = versionsFile
    )
    updateGradle()
    updatePlugins(
        pluginsFile = pluginsFile,
        versionsFile = versionsFile
    )
}
