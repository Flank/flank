package flank.scripts.utils

import com.github.kittinunf.fuel.Fuel
import java.io.File

fun download(
    srcUrl: String,
    dstFile: String
) {
    Fuel.download(srcUrl)
        .fileDestination { _, _ -> File(dstFile) }
        .responseString()
}
