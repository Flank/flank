@file:JvmName("Main")
package ftl

import ftl.presentation.cli.MainCommand
import ftl.reports.startDurationMeasurement
import ftl.run.exception.withGlobalExceptionHandling
import picocli.CommandLine

fun main(args: Array<String>) {
    withGlobalExceptionHandling {
        startDurationMeasurement()
        CommandLine(MainCommand()).execute(*args)
    }
}
