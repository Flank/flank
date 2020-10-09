@file:Repository("https://dl.bintray.com/jakubriegel/kotlin-shell")
@file:DependsOn("eu.jrie.jetbrains:kotlin-shell-core:0.2.1")
@file:DependsOn("org.slf4j:slf4j-simple:1.7.28")

@file:Import("updateLibs/downloadFiles.main.kts")

@file:CompilerOptions("-Xopt-in=kotlin.RequiresOptIn")
@file:OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)

import eu.jrie.jetbrains.kotlinshell.shell.*
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardCopyOption

var containsPip = false
shell {
    val exitCode = kotlin.runCatching {
        val process = "pip -V"()
        process.pcb.exitCode
    }.getOrDefault(1)
    containsPip = exitCode == 0
}
if (!containsPip) {
    downloadFile(
        url = "https://bootstrap.pypa.io/get-pip.py",
        destinationPath = "get-pip.py"
    )
    shell {
        "python get-pip.py"()
    }
}

shell {
    "pip install google-apis-client-generator"()
}

val apiPath = Paths.get("test_api").toString()
Paths.get(apiPath, "src").toFile().deleteRecursively()
shell {
    val generateLibraryCommand = "generate_library " +
        "--input=./json/testing_v1.json " +
        "--language=java " +
        "--package_path=api/services " +
        "--output_dir=./test_api/src/main/java"
    generateLibraryCommand()
}

Files.move(
    Paths.get(apiPath, "src", "main", "java", "pom.xml"),
    Paths.get(apiPath,  "pom.xml"),
    StandardCopyOption.REPLACE_EXISTING
)
