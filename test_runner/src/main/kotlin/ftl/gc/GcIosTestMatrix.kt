package ftl.gc

import com.google.testing.Testing
import com.google.testing.model.ClientInfo
import com.google.testing.model.EnvironmentMatrix
import com.google.testing.model.GoogleCloudStorage
import com.google.testing.model.IosDeviceList
import com.google.testing.model.IosTestSetup
import com.google.testing.model.ResultStorage
import com.google.testing.model.TestMatrix
import com.google.testing.model.TestSpecification
import com.google.testing.model.ToolResultsHistory
import ftl.args.IosArgs
import ftl.client.google.GcTesting
import ftl.client.google.run.mapGcsPathsToFileReference
import ftl.client.google.run.mapToIosDeviceFiles
import ftl.client.google.run.toClientInfoDetailList
import ftl.client.google.run.toIosDeviceFile
import ftl.gc.ios.setupIosTest
import ftl.run.exception.FlankGeneralError
import ftl.run.model.IosTestContext
import ftl.util.timeoutToSeconds

object GcIosTestMatrix {

    @Suppress("LongParameterList")
    fun build(
        iosDeviceList: IosDeviceList,
        args: IosArgs,
        iosTestContext: IosTestContext,
        toolResultsHistory: ToolResultsHistory,
        otherFiles: Map<String, String>,
        additionalIpasGcsPaths: List<String>
    ): Testing.Projects.TestMatrices.Create {
        val clientInfo = ClientInfo()
            .setName("Flank")
            .setClientInfoDetails(args.clientDetails?.toClientInfoDetailList())

        val iOSTestSetup = IosTestSetup()
            .setNetworkProfile(args.networkProfile)
            .setPushFiles(otherFiles.mapToIosDeviceFiles())
            .setAdditionalIpas(additionalIpasGcsPaths.mapGcsPathsToFileReference())
            .setPullDirectories(args.directoriesToPull.toIosDeviceFiles())

        val testTimeoutSeconds = timeoutToSeconds(args.testTimeout)

        val testSpecification = TestSpecification()
            .setDisableVideoRecording(!args.recordVideo)
            .setTestTimeout("${testTimeoutSeconds}s")
            .setIosTestSetup(iOSTestSetup)
            .setupIosTest(iosTestContext)

        val resultStorage = ResultStorage()
            .setGoogleCloudStorage(GoogleCloudStorage().setGcsPath(iosTestContext.matrixGcsPath))
            .setToolResultsHistory(toolResultsHistory)

        val environmentMatrix = EnvironmentMatrix().setIosDeviceList(iosDeviceList)

        val testMatrix = TestMatrix()
            .setClientInfo(clientInfo)
            .setTestSpecification(testSpecification)
            .setEnvironmentMatrix(environmentMatrix)
            .setResultStorage(resultStorage)
            .setFlakyTestAttempts(args.flakyTestAttempts)
            .setFailFast(args.failFast)

        try {
            return GcTesting.get.projects().testMatrices().create(args.project, testMatrix)
        } catch (e: Exception) {
            throw FlankGeneralError(e)
        }
    }
}

private fun List<String>.toIosDeviceFiles() = map { path -> toIosDeviceFile(path) }
