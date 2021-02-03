package flank.scripts.ops.dependencies

val Dependency.groupWithName get() = "$group:$name:"

val Dependency.versionToUpdate
    get() = availableVersion?.release
        ?: availableVersion?.milestone
        ?: availableVersion?.integration
        ?: version

fun GradleDependency.needsUpdate() = running.version < current.version || running.version < releaseCandidate.version
