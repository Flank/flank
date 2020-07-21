package ftl.environment

import com.google.api.services.testing.model.NetworkConfiguration
import ftl.util.buildTable

fun List<NetworkConfiguration>.asPrintableTable() = createProfileDetails().createNetworkProfileTable()

private fun List<NetworkConfiguration>.createProfileDetails() = fold(mutableMapOf<String, MutableList<String>>()) { table, networkConfiguration ->
    table.apply {
        getOrCreateList(PROFILE_ID).add(" ")
        getOrCreateList(PROFILE_ID).add(networkConfiguration.id)


        getOrCreateList(RULE).add(RULE_UP)
        getOrCreateList(RULE).add(RULE_DOWN)

        getOrCreateList(DELAY).add(networkConfiguration.upRule.delay.orEmpty())
        getOrCreateList(DELAY).add(networkConfiguration.downRule.delay.orEmpty())

        getOrCreateList(LOSS_RATION).add(networkConfiguration.upRule.packetLossRatio?.toString().orEmpty())
        getOrCreateList(LOSS_RATION).add(networkConfiguration.downRule.packetLossRatio?.toString().orEmpty())

        getOrCreateList(DUPLICATION_RATION).add(networkConfiguration.upRule.packetDuplicationRatio?.toString().orEmpty())
        getOrCreateList(DUPLICATION_RATION).add(networkConfiguration.downRule.packetDuplicationRatio?.toString().orEmpty())

        getOrCreateList(BANDWIDTH).add(networkConfiguration.upRule.bandwidth?.toString().orEmpty())
        getOrCreateList(BANDWIDTH).add(networkConfiguration.downRule.bandwidth?.toString().orEmpty())

        getOrCreateList(BURST).add(networkConfiguration.upRule.burst?.toString().orEmpty())
        getOrCreateList(BURST).add(networkConfiguration.downRule.burst?.toString().orEmpty())
    }
}


private fun TestEnvironmentInfo.createNetworkProfileTable() = buildTable(
    createTableColumnFor(PROFILE_ID),
    createTableColumnFor(RULE),
    createTableColumnFor(DELAY),
    createTableColumnFor(LOSS_RATION),
    createTableColumnFor(DUPLICATION_RATION),
    createTableColumnFor(BANDWIDTH),
    createTableColumnFor(BURST)
)


private const val PROFILE_ID = "PROFILE_ID"

private const val RULE = "RULE"
private const val RULE_UP = "up"
private const val RULE_DOWN = "down"
private const val DELAY = "DELAY"
private const val LOSS_RATION = "LOSS_RATIO"
private const val DUPLICATION_RATION = "DUPLICATION_RATIO"
private const val BANDWIDTH = "BANDWIDTH"
private const val BURST = "BURST"
