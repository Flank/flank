package flank.junit.mapper

import flank.junit.JUnit

internal fun List<JUnit.TestResult>.mapToTestSuites() = this
    .groupBy { case -> case.suiteName }
    .map { (suiteName, cases: List<JUnit.TestResult>) ->
        JUnit.Suite(
            name = suiteName,
            tests = cases.size,
            failures = cases.count { it.status == JUnit.TestResult.Status.Failed },
            errors = cases.count { it.status == JUnit.TestResult.Status.Error },
            skipped = cases.count { it.status == JUnit.TestResult.Status.Skipped },
            timestamp = JUnit.dateFormat.format(cases.map { it.startAt }.minOrNull() ?: 0),
            time = cases.filterNot { it.status == JUnit.TestResult.Status.Skipped }.run {
                if (isEmpty()) 0.0
                else (last().endsAt - first().startAt).toDouble() / 1000
            },
            testcases = cases.map { case ->
                JUnit.Case(
                    name = case.testName,
                    classname = case.className,
                    time = (case.endsAt - case.startAt).toDouble() / 1000,
                    error = if (case.status == JUnit.TestResult.Status.Error) case.stack else emptyList(),
                    failure = if (case.status == JUnit.TestResult.Status.Failed) case.stack else emptyList(),
                )
            }
        )
    }
