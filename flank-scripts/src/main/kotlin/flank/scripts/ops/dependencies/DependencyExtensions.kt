package flank.scripts.ops.dependencies

val Dependency.versionToUpdate
    get() = availableVersion?.release
        ?: availableVersion?.milestone
        ?: availableVersion?.integration
        ?: version
