package flank.scripts.ops.shell.ios

import com.github.ajalt.clikt.core.CliktCommand
import flank.common.iOSTestProjectsPath
import flank.scripts.ops.shell.ops.EARL_GREY_EXAMPLE
import flank.scripts.ops.shell.utils.failIfWindows
import flank.scripts.utils.downloadCocoaPodsIfNeeded
import flank.scripts.utils.installPodsIfNeeded
import java.nio.file.Paths

object SetupIosEnvCommand : CliktCommand(name = "setup_ios_env", help = "Build ios app with tests") {
    override fun run() {
        failIfWindows()
        downloadCocoaPodsIfNeeded()
        installPodsIfNeeded(Paths.get(iOSTestProjectsPath, EARL_GREY_EXAMPLE))
    }
}
