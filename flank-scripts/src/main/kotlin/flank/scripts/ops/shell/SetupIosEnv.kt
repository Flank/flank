package flank.scripts.ops.shell

import flank.common.iOSTestProjectsPath
import flank.scripts.ops.shell.buildexample.ios.EARL_GREY_EXAMPLE
import flank.scripts.utils.downloadCocoaPodsIfNeeded
import flank.scripts.utils.failIfWindows
import flank.scripts.utils.installPodsIfNeeded
import java.nio.file.Paths

fun setupIosEnv() {
    failIfWindows()
    downloadCocoaPodsIfNeeded()
    installPodsIfNeeded(Paths.get(iOSTestProjectsPath, EARL_GREY_EXAMPLE))
}
