@file:Repository("https://dl.bintray.com/jakubriegel/kotlin-shell")
@file:DependsOn("eu.jrie.jetbrains:kotlin-shell-core:0.2.1")
@file:DependsOn("org.slf4j:slf4j-simple:1.7.28")

@file:Import("../util/downloadSoftware.main.kts")

@file:CompilerOptions("-Xopt-in=kotlin.RequiresOptIn")
@file:OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)

import eu.jrie.jetbrains.kotlinshell.shell.*
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardCopyOption

downloadPipIfNeeded()

shell {
    "pip install google-apis-client-generator"()
}
val apiPath = Paths.get("test_api").toString()
val outputDirectory = Paths.get(apiPath, "src", "main", "java").toString()
val testingJsonInput = Paths.get("json", "testing_v1.json").toString()
Paths.get(apiPath, "src").toFile().deleteRecursively()
shell {
    val generateLibraryCommand = "generate_library " +
        "--input=$testingJsonInput " +
        "--language=java " +
        "--package_path=api/services " +
        "--output_dir=$outputDirectory"
    generateLibraryCommand()
}

Files.move(
    Paths.get(outputDirectory, "pom.xml"),
    Paths.get(apiPath,  "pom.xml"),
    StandardCopyOption.REPLACE_EXISTING
)
