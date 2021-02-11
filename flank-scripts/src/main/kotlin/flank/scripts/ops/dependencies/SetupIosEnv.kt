package flank.scripts.ops.dependencies

import flank.common.iOSTestProjectsPath
import flank.scripts.ops.common.EARL_GREY_EXAMPLE
import flank.scripts.utils.failIfWindows
import java.nio.file.Paths

fun setupIosEnv() {
    failIfWindows()
    downloadCocoaPodsIfNeeded()
    installPodsIfNeeded(Paths.get(iOSTestProjectsPath, EARL_GREY_EXAMPLE))
}
