package ftl.run.common

import flank.tool.analytics.sessionId
import ftl.args.IArgs
import java.nio.file.Paths

const val SESSION_ID_FILE = "session_id.txt"

fun IArgs.saveSessionId() = Paths.get(localResultDir, SESSION_ID_FILE).toFile().writeText(sessionId)
