package flank.scripts.ops.github

import flank.scripts.utils.runCommand

fun deleteOldRelease(tag: String) = "$DELETE_RELEASE_COMMAND $tag".runCommand()

private const val DELETE_RELEASE_COMMAND = "gh release delete"
