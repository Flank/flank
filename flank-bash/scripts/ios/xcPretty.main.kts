@file:Repository("https://dl.bintray.com/jakubriegel/kotlin-shell")
@file:DependsOn("eu.jrie.jetbrains:kotlin-shell-core:0.2.1")
@file:DependsOn("org.slf4j:slf4j-simple:1.7.28")

@file:CompilerOptions("-Xopt-in=kotlin.RequiresOptIn")
@file:OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)

import eu.jrie.jetbrains.kotlinshell.shell.shell

fun downloadXcPrettyIfNeeded() {
    shell {
        val exitCode = runCatching {
            val process = "command -v xcpretty"()
            process.pcb.exitCode
        }.getOrDefault(1)

        if(exitCode != 0) {
            "gem install xcpretty"()
        }
    }
}
