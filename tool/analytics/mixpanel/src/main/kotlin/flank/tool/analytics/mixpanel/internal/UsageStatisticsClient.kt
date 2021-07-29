package flank.tool.analytics.mixpanel.internal

import com.fasterxml.jackson.module.kotlin.jsonMapper
import com.fasterxml.jackson.module.kotlin.kotlinModule
import com.mixpanel.mixpanelapi.MessageBuilder
import com.mixpanel.mixpanelapi.MixpanelAPI
import kotlin.reflect.KClass

private const val MIXPANEL_API_TOKEN = "d9728b2c8e6ca9fd6de1fcd32dd8cdc2"

internal var blockSendUsageStatistics: Boolean = false

internal val messageBuilder by lazy {
    MessageBuilder(MIXPANEL_API_TOKEN)
}

internal val apiClient by lazy {
    MixpanelAPI()
}

internal val objectMapper by lazy {
    jsonMapper {
        addModule(kotlinModule())
    }
}

fun initializeStatisticsClient(blockUsageStatistics: Boolean, vararg statisticClasses: KClass<*>) {
    if (classesForStatistics != null) return

    blockSendUsageStatistics = blockUsageStatistics
    classesForStatistics = statisticClasses.asList()
}
