package flank.scripts.ops.release.hub

import com.github.kittinunf.result.Result
import flank.scripts.github.deleteOldTag
import kotlinx.coroutines.runBlocking
import java.lang.Exception

fun tryDeleteOldTag(
    gitTag: String,
    username: String,
    token: String,
    success: (response: Result.Success<ByteArray>) -> Unit,
    error: (response: Result.Failure<Exception>) -> Unit
) = runBlocking {
    when (val response = deleteOldTag(gitTag, username, token)) {
        is Result.Success -> success(response)
        is Result.Failure -> error(response)
    }
}
