package ftl.run.common

import ftl.args.AndroidArgs
import ftl.args.IArgs
import ftl.args.IosArgs
import ftl.config.FtlConstants
import ftl.util.flankFatalError
import java.nio.file.Paths

/** Reads in the last matrices from the localResultDir folder **/
internal fun getLastArgs(args: IArgs): IArgs {
    val lastRun = args.getLastGcsPath()

    if (lastRun == null) {
        flankFatalError("no runs found in results/ folder")
    }

    val iosConfig = Paths.get(args.localResultDir, lastRun, FtlConstants.defaultIosConfig)
    val androidConfig = Paths.get(args.localResultDir, lastRun, FtlConstants.defaultAndroidConfig)

    when {
        iosConfig.toFile().exists() -> return IosArgs.load(iosConfig)
        androidConfig.toFile().exists() -> return AndroidArgs.load(androidConfig)
        else -> flankFatalError("No config file found in the last run folder: $lastRun")
    }

    throw RuntimeException("should not happen")
}
