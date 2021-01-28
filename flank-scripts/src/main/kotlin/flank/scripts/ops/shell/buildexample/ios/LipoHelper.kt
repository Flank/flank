package flank.scripts.ops.shell.ios

fun createLipoCommand(
    outputPath: String,
    vararg files: String
) = "lipo -create ${files.joinToString(" ")} -output $outputPath"
