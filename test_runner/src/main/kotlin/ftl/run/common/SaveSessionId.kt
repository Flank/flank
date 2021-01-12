package ftl.run.common

import ftl.args.IArgs
import ftl.util.sessionId
import java.nio.file.Paths

const val SESSION_ID_FILE = "session_id.txt"

fun IArgs.saveSessionId() = Paths.get(localResultDir, SESSION_ID_FILE).toFile().writeText(sessionId)
