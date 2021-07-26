package flank.corellium.domain.test.android.task

import flank.corellium.domain.TestAndroid
import flank.corellium.domain.TestAndroid.ProcessResults
import flank.corellium.domain.TestAndroid.TestExecution
import flank.corellium.domain.TestAndroid.context
import flank.exection.parallel.from
import flank.exection.parallel.using
import flank.instrument.log.Instrument
import flank.instrument.log.Instrument.Code.errors

val processResults = ProcessResults from setOf(
    TestExecution
) using context {
    if (args.reportConfig.processResults) return@context emptyList()

    val flakyTests: Set<String> = (shardResults + rerunResults)
        .flatMap { result ->
            result.value
                .filterIsInstance<Instrument.Status>()
                .map { status -> status.run { name to (code in errors) } }
        }
        .groupBy(
            keySelector = { (name, _) -> name },
            valueTransform = { (_, failed) -> failed }
        )
        .filterValues { results -> results.groupBy { it }.size == 2 }
        .keys

    shardResults.map { result ->
        result.copy(
            flakes = result.value.mapNotNull { status ->
                when {
                    status !is Instrument.Status -> null
                    status.name !in flakyTests -> null
                    else -> status.name
                }
            }.toSet()
        )
    }
}

private val TestAndroid.Args.ReportConfig.processResults: Boolean
    get() = jUnitXml.values.flatten().any { it == TestAndroid.Args.ReportConfig.JUnitType.Processed }

private val Instrument.Status.name get() = details.run { "$className#$testName" }
