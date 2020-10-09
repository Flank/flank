@file:Repository("https://dl.bintray.com/kittinunf/maven/com/github/kittinunf/fuel/fuel/")
@file:DependsOn("com.github.kittinunf.fuel:fuel:2.3.0")
@file:DependsOn("com.github.kittinunf.fuel:fuel-coroutines:2.3.0")

import com.github.kittinunf.fuel.Fuel
import java.io.File

fun downloadFile(
    url: String,
    destinationPath: String
) {
    println("Downloading from $url")
    var lastUpdate = 0L
    Fuel.download(url)
        .fileDestination { _, _ -> File(destinationPath) }
        .progress { readBytes, totalBytes ->
            if (System.currentTimeMillis() - lastUpdate > 2000) {
                lastUpdate = System.currentTimeMillis()
                val progress = readBytes.toFloat() / totalBytes.toFloat() * 100
                println("Bytes downloaded $readBytes / $totalBytes ($progress %)")
            }
        }
        .response()
}

