package ftl.environment

import ftl.api.NetworkProfile
import ftl.api.fetchNetworkProfiles

fun networkProfileDescription(profile: String) = fetchNetworkProfiles()
    .find { it.id.equals(profile, ignoreCase = true) }
    .toNullProof()
    .prepareDescription()
    ?: "Unable to fetch profile [$profile] description"

private fun NetworkProfile?.toNullProof() = this?.run {
    NetworkConfigurationWrapper(
        downRule = wrappedOrEmpty(downRule),
        upRule = wrappedOrEmpty(upRule),
        id = id.toStringOrUnableToFetch()
    )
}

private fun wrappedOrEmpty(rule: NetworkProfile.Rule?) = rule?.let {
    Rule(
        bandwidth = it.bandwidth.toStringOrUnableToFetch(),
        delay = it.delay.toStringOrUnableToFetch(),
        packetLossRatio = it.packetLossRatio.toStringOrUnableToFetch()
    )
} ?: emptyRule

private const val UNABLE = "[Unable to fetch]"

private fun Any?.toStringOrUnableToFetch() = this?.toString() ?: UNABLE

private val emptyRule: Rule
    get() = Rule(UNABLE, UNABLE, UNABLE)

private fun NetworkConfigurationWrapper?.prepareDescription() = this?.run {
    """
downRule:
  bandwidth: ${downRule.bandwidth}
  delay: ${downRule.delay}
  packetLossRatio: ${downRule.packetLossRatio}
id: $id
upRule:
  bandwidth: ${upRule.bandwidth}
  delay: ${upRule.delay}
  packetLossRatio: ${upRule.packetLossRatio}
    """.trimIndent()
}

private data class NetworkConfigurationWrapper(val downRule: Rule, val upRule: Rule, val id: String)

private data class Rule(val bandwidth: String, val delay: String, val packetLossRatio: String)
