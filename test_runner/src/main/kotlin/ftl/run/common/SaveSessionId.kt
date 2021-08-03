package ftl.run.common

import flank.tool.analytics.mixpanel.Mixpanel
import ftl.args.IArgs
import java.nio.file.Paths

const val SESSION_ID_FILE = "session_id.txt"

fun IArgs.saveSessionId() = Paths.get(localResultDir, SESSION_ID_FILE).toFile().writeText(Mixpanel.sessionId)
