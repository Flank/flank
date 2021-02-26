package flank.scripts.ops.integrationtests

import flank.scripts.ops.integrationtests.common.ITResult
import flank.scripts.ops.integrationtests.common.ITRunState

val dummyITRunState = ITRunState(
    windowsBSUrl = "",
    windowsResult = ITResult.FAILURE,
    macOsBSUrl = "",
    macOsResult = ITResult.FAILURE,
    linuxBSUrl = "",
    linuxResult = ITResult.FAILURE,
)
