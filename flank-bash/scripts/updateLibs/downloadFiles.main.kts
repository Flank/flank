@file:Repository("https://dl.bintray.com/kittinunf/maven/com/github/kittinunf/fuel/fuel/")
@file:DependsOn("com.github.kittinunf.fuel:fuel:2.3.0")
@file:DependsOn("com.github.kittinunf.fuel:fuel-coroutines:2.3.0")

import com.github.kittinunf.fuel.Fuel
import java.io.File

fun downloadFile(
    url: String,
    destinationPath: String
)  {
    println("Downloading from $url")
    Fuel.download(url)
        .fileDestination { _, _ -> File(destinationPath) }
        .response()
}

