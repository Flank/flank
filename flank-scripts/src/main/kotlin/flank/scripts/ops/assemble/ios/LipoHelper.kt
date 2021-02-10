package flank.scripts.ops.assemble.ios

fun createLipoCommand(
    outputPath: String,
    vararg files: String
) = "lipo -create ${files.joinToString(" ")} -output $outputPath"
