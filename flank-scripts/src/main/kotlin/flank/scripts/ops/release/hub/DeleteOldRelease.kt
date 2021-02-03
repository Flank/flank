package flank.scripts.ops.release.hub

import flank.scripts.utils.runCommand

fun deleteOldRelease(tag: String) = "$DELETE_RELEASE_COMMAND $tag".runCommand()

private const val DELETE_RELEASE_COMMAND = "hub release delete"
