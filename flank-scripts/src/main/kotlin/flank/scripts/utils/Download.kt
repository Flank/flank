package flank.scripts.utils

import com.github.kittinunf.fuel.Fuel
import java.io.File

fun downloadFile(
    srcUrl: String,
    destinationPath: String
) {
    Fuel.download(srcUrl)
        .fileDestination { _, _ -> File(destinationPath) }
        .responseString()
}
