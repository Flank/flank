@file:Repository("https://repo1.maven.org/maven2/org/rauschig/jarchivelib/")
@file:DependsOn("org.rauschig:jarchivelib:1.1.0")
@file:DependsOn("org.tukaani:xz:1.0")

@file:Repository("https://dl.bintray.com/kittinunf/maven/com/github/kittinunf/fuel/fuel/")
@file:DependsOn("com.github.kittinunf.fuel:fuel:2.3.0")
@file:DependsOn("com.github.kittinunf.fuel:fuel-coroutines:2.3.0")

@file:Import("./updateLibs/updateAtomic.main.kts")
@file:Import("./updateLibs/updateLlvm.main.kts")
@file:Import("./updateLibs/updateSwift.main.kts")

@file:DependsOn("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.3.9")

import java.io.File
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.launch

runBlocking {
    launch { updateAtomic() }
    launch { updateLlvm() }
    launch { updateSwift() }
}

println("Binaries updated!")
