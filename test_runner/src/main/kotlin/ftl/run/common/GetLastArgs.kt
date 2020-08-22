package ftl.run.common

import ftl.args.AndroidArgs
import ftl.args.IArgs
import ftl.args.IosArgs
import ftl.config.FtlConstants
import ftl.run.exception.FlankGeneralError
import java.nio.file.Paths

/** Reads in the last matrices from the localResultDir folder **/
internal fun getLastArgs(args: IArgs): IArgs {
    val lastRun = args.getLastGcsPath() ?: throw FlankGeneralError("no runs found in results/ folder")

    val iosConfig = Paths.get(args.localResultDir, lastRun, FtlConstants.defaultIosConfig)
    val androidConfig = Paths.get(args.localResultDir, lastRun, FtlConstants.defaultAndroidConfig)

    return when {
        iosConfig.toFile().exists() -> IosArgs.load(iosConfig)
        androidConfig.toFile().exists() -> AndroidArgs.load(androidConfig)
        else -> throw FlankGeneralError("No config file found in the last run folder: $lastRun")
    }
}
