package flank.scripts.ops.dependencies

import flank.scripts.ops.dependencies.common.Dependency

val Dependency.versionToUpdate
    get() = availableVersion?.release
        ?: availableVersion?.milestone
        ?: availableVersion?.integration
        ?: version
