package flank.scripts.ops.github

import com.github.kittinunf.result.Result
import flank.common.logLn
import flank.scripts.data.github.deleteOldTag

fun tryDeleteOldTag(
    gitTag: String,
    username: String,
    token: String
) = tryDeleteOldTag(
    gitTag = gitTag,
    username = username,
    token = token,
    success = { logLn("Tag $gitTag was deleted") },
    error = { logLn(it.error) }
)

fun tryDeleteOldTag(
    gitTag: String,
    username: String,
    token: String,
    success: (response: Result.Success<ByteArray>) -> Unit,
    error: (response: Result.Failure<Exception>) -> Unit
) {
    when (val response = deleteOldTag(gitTag, username, token)) {
        is Result.Success -> success(response)
        is Result.Failure -> error(response)
    }
}
