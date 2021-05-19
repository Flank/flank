package flank.corellium.domain.run.test.android.step

import flank.corellium.domain.RunTestCorelliumAndroid
import java.io.File

/**
 * The step is creating the output directory for execution results.
 */
internal fun RunTestCorelliumAndroid.Context.createOutputDir() = RunTestCorelliumAndroid.step {
    println("* Preparing output directory")
    require(args.outputDir.isNotEmpty())
    val dir = File(args.outputDir)
    when {
        !dir.exists() -> {
            dir.mkdirs()
            println("Created ${dir.absolutePath}")
        }
        !dir.isDirectory -> {
            throw IllegalStateException("Cannot create output directory, file ${dir.absolutePath} already exist")
        }
        else -> {
            println(println("Already exist ${dir.absolutePath}"))
        }
    }
    this
}
