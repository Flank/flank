package ftl.analytics

import ftl.args.IosArgs
import ftl.args.blockSendingUsageStatistics

fun IosArgs.sendConfiguration() = takeUnless { blockSendingUsageStatistics }?.let {
    registerUser()
    IosArgs.default().let { defaultArgs ->
        objectToMap().filterNonCommonArgs().getNonDefaultArgs(defaultArgs.objectToMap())
            .plus(commonArgs.objectToMap().getNonDefaultArgs(defaultArgs.commonArgs.objectToMap()))
            .createEvent(project)
            .sendMessage()
    }
}
