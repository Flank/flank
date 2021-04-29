package ftl.domain

import ftl.environment.networkProfileDescription
import ftl.presentation.Output
import ftl.run.exception.FlankConfigurationError

interface DescribeNetworkProfiles : Output {
    val profileId: String
}

operator fun DescribeNetworkProfiles.invoke() {
    if (profileId.isBlank()) throw FlankConfigurationError("Argument PROFILE_ID must be specified.")
    networkProfileDescription(profileId).out()
}
