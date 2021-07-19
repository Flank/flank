package flank.corellium.domain.run.test.android.step

import flank.corellium.domain.RunTestCorelliumAndroid.AlreadyExist
import flank.corellium.domain.RunTestCorelliumAndroid.Created
import flank.corellium.domain.RunTestCorelliumAndroid.OutputDir
import flank.corellium.domain.RunTestCorelliumAndroid.context
import flank.exection.parallel.using
import java.io.File

/**
 * The step is creating the output directory for execution results.
 */
internal val createOutputDir = OutputDir using context {
    require(args.outputDir.isNotEmpty())
    val dir = File(args.outputDir)
    when {
        !dir.exists() -> {
            dir.mkdirs()
            Created(dir).out()
        }
        !dir.isDirectory -> {
            throw IllegalStateException("Cannot create output directory, file ${dir.path} already exist")
        }
        else -> {
            AlreadyExist(dir).out()
        }
    }
}
