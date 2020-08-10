package ftl.environment

import com.google.api.services.testing.model.NetworkConfiguration
import com.google.api.services.testing.model.TrafficRule

fun networkProfileDescription(profile: String) = getNetworkConfiguration()
        .find { it.id?.toUpperCase() == profile.toUpperCase() }
        .toNullProof()
        .prepareDescription()
        ?: "Unable to fetch profile [$profile] description"

private fun NetworkConfiguration?.toNullProof() = this?.run {
    NetworkConfigurationWrapper(
            downRule = wrappedOrEmpty(downRule),
            upRule = wrappedOrEmpty(upRule),
            id = id.toStringOrUnableToFetch()
    )
}

private fun wrappedOrEmpty(rule: TrafficRule?) = rule?.let {
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
