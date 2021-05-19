package flank.corellium.api

import kotlinx.coroutines.flow.Flow

/**
 * The configuration of test plan for android tests executions.
 */
object AndroidTestPlan {

    /**
     * The configuration of the Android test plan.
     *
     * @property instances the map of instance ids with related `am instrument` commands to execute.
     */
    data class Config(
        val instances: Map<InstanceId, List<AmInstrumentCommand>>
    )

    /**
     * Execute tests on android instances using specified configuration.
     */
    fun interface Execute : (Config) -> List<Flow<AmInstrumentOutputLine>>
}

private typealias InstanceId = String

private typealias AmInstrumentCommand = String

private typealias AmInstrumentOutputLine = String
