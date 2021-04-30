package ftl.environment

import ftl.api.NetworkProfile
import ftl.api.fetchNetworkProfiles

fun networkProfileDescription(profile: String): NetworkProfile? = fetchNetworkProfiles()
    .find { it.id.equals(profile, ignoreCase = true) }

fun NetworkProfile.prepareDescription(): String =
    """
downRule:
  bandwidth: ${downRule.bandwidth ?: UNABLE_TO_FETCH}
  delay: ${downRule.delay ?: UNABLE_TO_FETCH}
  packetLossRatio: ${downRule.packetLossRatio ?: UNABLE_TO_FETCH}
id: $id
upRule:
  bandwidth: ${upRule.bandwidth ?: UNABLE_TO_FETCH}
  delay: ${upRule.delay ?: UNABLE_TO_FETCH}
  packetLossRatio: ${upRule.packetLossRatio ?: UNABLE_TO_FETCH}
    """.trimIndent()

private const val UNABLE_TO_FETCH = "[Unable to fetch]"
