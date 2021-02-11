package flank.scripts.ops.assemble.ios

import flank.common.currentPath
import flank.scripts.utils.failIfWindows
import flank.scripts.utils.runCommand
import java.nio.file.Path
import java.nio.file.Paths

fun runFtlLocal(deviceId: String) {
    failIfWindows()
    val dataPath: Path = Paths.get(currentPath.toString(), "dd_tmp", "Build", "Products")

    val xcodeCommand = "xcodebuild test-without-building " +
        " -xctestrun $dataPath/*.xctestrun " +
        "-derivedDataPath $dataPath " +
        "-destination 'id=$deviceId'"

    xcodeCommand.runCommand()
}
