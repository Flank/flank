import java.nio.file.Paths

val rootDirectoryPath = Paths
    .get("")
    .toAbsolutePath()
    .parent
    .toString()

val currentPath = Paths.get("")

val testProjectsPath = Paths.get(rootDirectoryPath, "test_projects").toString()
val androidTestProjectsPath = Paths.get(testProjectsPath, "android").toString()
val iOsTestProjectsPath = Paths.get(testProjectsPath, "ios").toString()
val flankFixturesTmpPath =
    Paths.get(rootDirectoryPath, "test_runner", "src", "test", "kotlin", "ftl", "fixtures", "tmp").toString()
