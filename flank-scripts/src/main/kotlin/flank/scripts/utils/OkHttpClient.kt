package flank.scripts.utils

import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.ResponseBody
import java.nio.file.Files
import java.nio.file.Paths

fun OkHttpClient.download(
    srcUrl: String,
    dstFile: String
) {
    val request = Request.Builder().url(srcUrl).build()
    val response = newCall(request).execute()
    val body: ResponseBody = response.body ?: throw Exception("null response body downloading $srcUrl")

    Files.write(Paths.get(dstFile), body.bytes())
}
