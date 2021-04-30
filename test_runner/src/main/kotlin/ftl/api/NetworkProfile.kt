package ftl.api

import ftl.adapter.NetworkProfileFetch

val fetchNetworkProfiles: NetworkProfile.Fetch get() = NetworkProfileFetch

data class NetworkProfile(
    val id: String,
    val downRule: Rule,
    val upRule: Rule
) {
    data class Rule(
        val bandwidth: Float?,
        val delay: String?,
        val packetLossRatio: Float?,
        val packetDuplicationRatio: Float?,
        val burst: Float?
    )

    data class ErrorMessage(
        val message: String
    )

    interface Fetch : () -> List<NetworkProfile>
}
