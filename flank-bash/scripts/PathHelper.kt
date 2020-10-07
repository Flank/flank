import java.nio.file.Paths

val rootDirectoryPath = Paths
    .get("")
    .toAbsolutePath()
    .parent
    .toString()
