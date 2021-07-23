package flank.junit.internal

import flank.junit.JUnit
import flank.junit.mapper.mapToTestResults
import flank.junit.mapper.parseJUnitReport
import java.io.File

internal val parseJUnitTestResults = JUnit.TestResult.Parse { path ->
    val file = File(path)
    when {
        file.isFile -> sequenceOf(file)
        file.isDirectory -> file.walkBottomUp()
            .filter { next -> next.name == JUnit.REPORT_FILE_NAME }
            .sortedByDescending(File::lastModified)
        else -> emptySequence()
    }.map { next: File ->
        next.reader().parseJUnitReport().testsuites.mapToTestResults()
    }
}
