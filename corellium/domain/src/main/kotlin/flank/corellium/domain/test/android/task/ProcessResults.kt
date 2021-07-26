package flank.corellium.domain.test.android.task

import flank.corellium.domain.TestAndroid
import flank.corellium.domain.TestAndroid.Args.Report.JUnit
import flank.corellium.domain.TestAndroid.Dispatch
import flank.corellium.domain.TestAndroid.ExecuteTests
import flank.corellium.domain.TestAndroid.ProcessedResults
import flank.corellium.domain.TestAndroid.context
import flank.exection.parallel.from
import flank.exection.parallel.using
import flank.instrument.log.Instrument
import flank.instrument.log.Instrument.Code.PASSED
import flank.instrument.log.Instrument.Code.SKIPPED
import flank.instrument.log.Instrument.Code.errors

/**
 * Process raw test results according to [TestAndroid.Args.Report] requirements.
 */
val processResults = ProcessedResults from setOf(
    ExecuteTests,
) using context {
    val codes = rawResults.mapTestCodes()
    val shards = rawResults.filter { result -> result.data.type == Dispatch.Type.Shard }

    args.junitReport.mapValues { (_, types) ->
        shards.filter(codes.testNamesBy(types)).run {
            if (JUnit.Type.Flaky !in types) this
            else update(codes.testNamesBy(JUnit.Type.Flaky))
        }
    }
}

private typealias TestResults = List<TestAndroid.Device.Result>
private typealias TestCodes = Map<String, List<Int>>

private fun TestResults.mapTestCodes(): TestCodes = this
    .flatMap { result ->
        result.value
            .filterIsInstance<Instrument.Status>()
            .map { status -> status.run { name to code } }
    }
    .groupBy(
        keySelector = { (name, _) -> name },
        valueTransform = { (_, codes) -> codes }
    )

private fun TestCodes.testNamesBy(types: Set<JUnit.Type>): Set<String> =
    types.flatMap { testNamesBy(it) }.toSet()

private fun TestCodes.testNamesBy(type: JUnit.Type): Set<String> {
    fun <K, V> Map<K, V>.keysByValues(predicate: V.() -> Boolean): Set<K> = filterValues(predicate).keys
    return when (type) {
        JUnit.Type.Skipped -> keysByValues { all { code -> code == SKIPPED } }
        JUnit.Type.Passed -> keysByValues { all { code -> code == PASSED } }
        JUnit.Type.Failed -> keysByValues { all { code -> code in errors } }
        JUnit.Type.Flaky -> keysByValues { groupBy { code -> code in errors }.size == 2 }
    }
}

private fun TestResults.filter(testNames: Set<String>): TestResults = map { result ->
    result.copy(
        value = result.value
            .filterIsInstance<Instrument.Status>()
            .filter { status -> status.name in testNames }
    )
}

private fun TestResults.update(flakyTests: Set<String>): TestResults =
    map { result ->
        result.copy(
            flakes = result.value
                .filterIsInstance<Instrument.Status>()
                .map(Instrument.Status::name)
                .filter(flakyTests::contains)
                .toSet()
        )
    }
