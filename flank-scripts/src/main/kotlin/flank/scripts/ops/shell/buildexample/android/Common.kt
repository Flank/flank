package flank.scripts.ops.shell.buildexample.android

import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardCopyOption

internal fun List<String>.canExecute(actionName: String) = isEmpty() || any { it.equals(actionName, ignoreCase = true) }

internal fun File.findApks() = walk().filter { it.extension == "apk" }

internal fun File.copyApkToDirectory(output: Path): Path = toPath().let { sourceFile ->
    if (!output.parent.toFile().exists()) Files.createDirectories(output.parent)
    Files.copy(sourceFile, output, StandardCopyOption.REPLACE_EXISTING)
}
