package ftl.environment

import ftl.api.NetworkProfile
import ftl.api.fetchNetworkProfiles

fun networkProfileDescription(profile: String) = fetchNetworkProfiles()
    .find { it.id.equals(profile, ignoreCase = true) }
    ?: "Unable to fetch profile [$profile] description"

fun NetworkProfile.prepareDescription() =
    """
downRule:
  bandwidth: ${downRule.bandwidth.orUnable()}
  delay: ${downRule.delay.orUnable()}
  packetLossRatio: ${downRule.packetLossRatio.orUnable()}
id: $id
upRule:
  bandwidth: ${upRule.bandwidth.orUnable()}
  delay: ${upRule.delay.orUnable()}
  packetLossRatio: ${upRule.packetLossRatio.orUnable()}
    """.trimIndent()

private fun Any?.orUnable() = this ?: "[Unable to fetch]"
