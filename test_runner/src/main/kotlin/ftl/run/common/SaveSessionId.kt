package ftl.run.common

import ftl.args.IArgs
import ftl.util.sessionId
import java.nio.file.Path

const val SESSION_ID_FILE = "session_id.txt"

fun IArgs.saveSessionId() = Path.of(localResultDir, SESSION_ID_FILE).toFile().writeText(sessionId)
