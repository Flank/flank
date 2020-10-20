package flank.scripts.shell.firebase

import com.github.ajalt.clikt.core.CliktCommand
import flank.scripts.shell.utils.currentPath
import flank.scripts.utils.downloadFile
import flank.scripts.utils.downloadSortJsonIfNeeded
import flank.scripts.utils.runCommand
import java.nio.file.Paths

class UpdateApiJsonCommand : CliktCommand(name = "update_api_json", help = "Contains all firebase commands") {
    override fun run() {
        val jsonDirectoryPath = Paths.get(currentPath.toString(), "json")
        val testingV1Path = Paths.get(jsonDirectoryPath.toString(), "testing_v1.json").toString()
        val testingV1Beta3Path = Paths.get(jsonDirectoryPath.toString(), "toolresults_v1beta3.json").toString()

        downloadFile(
            "https://raw.githubusercontent.com/Flank/gcloud_cli/master/google-cloud-sdk/lib/googlecloudsdk/third_party/apis/testing_v1.json",
            testingV1Path
        )

        downloadFile(
            "https://raw.githubusercontent.com/Flank/gcloud_cli/master/google-cloud-sdk/lib/googlecloudsdk/third_party/apis/toolresults_v1beta3.json",
            testingV1Beta3Path
        )

        downloadSortJsonIfNeeded()

        "sort-json $testingV1Path".runCommand()
        "sort-json $testingV1Beta3Path".runCommand()
    }
}
