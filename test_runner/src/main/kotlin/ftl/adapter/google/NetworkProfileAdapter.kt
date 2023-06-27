package ftl.adapter.google

import com.google.api.services.testing.model.NetworkConfiguration
import com.google.api.services.testing.model.TrafficRule
import ftl.api.NetworkProfile

internal fun List<NetworkConfiguration>.toApiModel() = map {
    NetworkProfile(
        it.id,
        it.downRule.toDataRule(),
        it.upRule.toDataRule()
    )
}

private fun TrafficRule.toDataRule() = NetworkProfile.Rule(
    bandwidth,
    delay,
    packetLossRatio,
    packetDuplicationRatio,
    burst
)
