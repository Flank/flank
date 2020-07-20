package ftl.environment

import com.google.api.services.testing.model.NetworkConfiguration
import ftl.util.buildTable
import ftl.util.buildTableWithPad

fun List<NetworkConfiguration>.asPrintableTable() = createProfileData().createNetworkProfileTables()

private fun List<NetworkConfiguration>.createProfileData() = createProfileHeaders().zip(createProfileDetails()) { header, details -> listOf(header, details) }.flatten()

private fun List<NetworkConfiguration>.createProfileHeaders() = map { it.createProfileHeader() }

private fun NetworkConfiguration.createProfileHeader() = mutableMapOf<String, MutableList<String>>().apply {
    getOrCreateList(PROFILE_ID).add(id)
}

private fun List<NetworkConfiguration>.createProfileDetails() = this.map { it.createProfileDetails() }

private fun NetworkConfiguration.createProfileDetails() = mutableMapOf<String, MutableList<String>>().apply {
    getOrCreateList(RULE).add(RULE_UP)
    getOrCreateList(RULE).add(RULE_DOWN)

    getOrCreateList(DELAY).add(upRule.delay.orEmpty())
    getOrCreateList(DELAY).add(downRule.delay.orEmpty())

    getOrCreateList(LOSS_RATION).add(upRule.packetLossRatio?.toString().orEmpty())
    getOrCreateList(LOSS_RATION).add(downRule.packetLossRatio?.toString().orEmpty())

    getOrCreateList(DUPLICATION_RATION).add(upRule.packetDuplicationRatio?.toString().orEmpty())
    getOrCreateList(DUPLICATION_RATION).add(downRule.packetDuplicationRatio?.toString().orEmpty())

    getOrCreateList(BANDWIDTH).add(upRule.bandwidth?.toString().orEmpty())
    getOrCreateList(BANDWIDTH).add(downRule.bandwidth?.toString().orEmpty())

    getOrCreateList(BURST).add(upRule.burst?.toString().orEmpty())
    getOrCreateList(BURST).add(downRule.burst?.toString().orEmpty())
}

private fun List<TestEnvironmentInfo>.createNetworkProfileTables() = map { it.createNetworkProfileTable() }
private fun TestEnvironmentInfo.createNetworkProfileTable() =
    if (this.containsKey(PROFILE_ID)) buildTable(createTableColumnFor(PROFILE_ID))
    else buildDetailsTable()

private fun TestEnvironmentInfo.buildDetailsTable(): String = buildTableWithPad(
    TABLE_PAD,
    createTableColumnFor(RULE),
    createTableColumnFor(DELAY),
    createTableColumnFor(LOSS_RATION),
    createTableColumnFor(DUPLICATION_RATION),
    createTableColumnFor(BANDWIDTH),
    createTableColumnFor(BURST)
)

private const val TABLE_PAD = 3

private const val PROFILE_ID = "PROFILE_ID"

private const val RULE = "RULE"
private const val RULE_UP = "up"
private const val RULE_DOWN = "down"
private const val DELAY = "DELAY"
private const val LOSS_RATION = "LOSS_RATION"
private const val DUPLICATION_RATION = "DUPLICATION_RATION"
private const val BANDWIDTH = "BANDWIDTH"
private const val BURST = "BURST"
