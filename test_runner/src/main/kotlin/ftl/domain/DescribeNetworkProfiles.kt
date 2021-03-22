package ftl.domain

import flank.common.logLn
import ftl.environment.networkProfileDescription
import ftl.run.exception.FlankConfigurationError

interface DescribeNetworkProfiles {
    val profileId: String
}

operator fun DescribeNetworkProfiles.invoke() {
    if (profileId.isBlank()) throw FlankConfigurationError("Argument PROFILE_ID must be specified.")
    logLn(networkProfileDescription(profileId))
}
