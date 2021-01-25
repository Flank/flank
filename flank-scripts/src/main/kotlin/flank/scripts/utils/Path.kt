package flank.scripts.utils

import flank.common.currentPath
import flank.common.goToRoot

fun getRootPathString() = goToRoot(currentPath).toAbsolutePath().toString()
fun getRootDirFile() = goToRoot(currentPath).toAbsolutePath().toFile()
fun getKtlintFilePath() = getRootPathString() + (if (isWindows) "\\" else "/") + "ktlint"
