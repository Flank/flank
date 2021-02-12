package flank.scripts.ops.dependencies.common

import flank.scripts.utils.Version

data class DependencyUpdate(
    val name: String,
    val valName: String,
    val oldVersion: Version,
    val newVersion: Version
)
