package flank.scripts.ops.shell.firebase.apiclient

import flank.common.config.flankGcloudCLIRepository
import flank.common.currentPath
import flank.common.downloadFile
import flank.scripts.utils.downloadSortJsonIfNeeded
import flank.scripts.utils.runCommand
import java.nio.file.Paths

fun updateApiJson() {

    val jsonDirectoryPath = Paths.get(currentPath.toString(), "firebase_apis", "json")
    val testingV1Path = Paths.get(jsonDirectoryPath.toString(), "testing_v1.json").toString()
    val testingV1Beta3Path = Paths.get(jsonDirectoryPath.toString(), "toolresults_v1beta3.json").toString()

    downloadFile(
        "https://raw.githubusercontent.com/$flankGcloudCLIRepository/master/google-cloud-sdk/lib/googlecloudsdk/third_party/apis/testing_v1.json",
        testingV1Path
    )

    downloadFile(
        "https://raw.githubusercontent.com/$flankGcloudCLIRepository/master/google-cloud-sdk/lib/googlecloudsdk/third_party/apis/toolresults_v1beta3.json",
        testingV1Beta3Path
    )

    downloadSortJsonIfNeeded()

    "sort-json $testingV1Path".runCommand()
    "sort-json $testingV1Beta3Path".runCommand()
}
