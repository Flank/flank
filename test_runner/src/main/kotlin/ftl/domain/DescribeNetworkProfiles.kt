package ftl.domain

import ftl.api.NetworkProfile
import ftl.environment.networkProfileDescription
import ftl.presentation.Output
import ftl.run.exception.FlankConfigurationError

interface DescribeNetworkProfiles : Output {
    val profileId: String
}

operator fun DescribeNetworkProfiles.invoke() {
    if (profileId.isBlank()) throw FlankConfigurationError("Argument PROFILE_ID must be specified.")
    val description = networkProfileDescription(profileId)
        ?: NetworkProfile.ErrorMessage("Unable to fetch profile [$profileId] description")

    description.out()
}
