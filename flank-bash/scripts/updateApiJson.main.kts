
@file:Repository("https://dl.bintray.com/jakubriegel/kotlin-shell")
@file:DependsOn("eu.jrie.jetbrains:kotlin-shell-core:0.2.1")
@file:DependsOn("org.slf4j:slf4j-simple:1.7.28")

@file:Import("updateLibs/downloadFiles.main.kts")

@file:OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)

import eu.jrie.jetbrains.kotlinshell.shell.shell
import java.nio.file.Paths

private val currentPath = Paths.get("").toString()
private val jsonDirectoryPath = Paths.get(currentPath, "json")
val testingV1Path = Paths.get(jsonDirectoryPath.toString(), "testing_v1.json").toString()
val testingV1Beta3Path = Paths.get(jsonDirectoryPath.toString(), "toolresults_v1beta3.json").toString()

jsonDirectoryPath.toFile().mkdir()

downloadFile(
    "https://raw.githubusercontent.com/Flank/gcloud_cli/master/google-cloud-sdk/lib/googlecloudsdk/third_party/apis/testing_v1.json",
    testingV1Path
)

downloadFile(
    "https://raw.githubusercontent.com/Flank/gcloud_cli/master/google-cloud-sdk/lib/googlecloudsdk/third_party/apis/toolresults_v1beta3.json",
    testingV1Beta3Path
)

shell {
    "sort-json $testingV1Path"()
    "sort-json $testingV1Beta3Path"()
}


