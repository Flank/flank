package ftl.analytics

import ftl.args.AndroidArgs
import ftl.args.blockSendingUsageStatistics

fun AndroidArgs.sendConfiguration() = takeUnless { blockSendingUsageStatistics }?.let {
    registerUser()
    AndroidArgs.default().let { defaultArgs ->
        objectToMap().filterNonCommonArgs().getNonDefaultArgs(defaultArgs.objectToMap())
            .plus(commonArgs.objectToMap().getNonDefaultArgs(defaultArgs.commonArgs.objectToMap()))
            .createEvent(project)
            .sendMessage()
    }
}
