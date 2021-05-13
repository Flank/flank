package ftl.presentation.cli.firebase.test.networkprofiles

import ftl.api.NetworkProfile
import ftl.environment.TestEnvironmentInfo
import ftl.environment.createTableColumnFor
import ftl.environment.getOrCreateList
import ftl.util.TableStyle
import ftl.util.buildTable

fun List<NetworkProfile>.toCliTable() = createConfigurationDetails().createConfigurationsTable()

private fun List<NetworkProfile>.createConfigurationDetails() = fold(mutableMapOf<String, MutableList<String>>()) { networkInfo, networkConfiguration ->
    networkInfo.apply {
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

private fun TestEnvironmentInfo.createConfigurationsTable() = buildTable(
    createTableColumnFor(PROFILE_ID),
    createTableColumnFor(RULE),
    createTableColumnFor(DELAY),
    createTableColumnFor(LOSS_RATION),
    createTableColumnFor(DUPLICATION_RATION),
    createTableColumnFor(BANDWIDTH),
    createTableColumnFor(BURST),
    tableStyle = TableStyle.ROW_SEPARATOR
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
