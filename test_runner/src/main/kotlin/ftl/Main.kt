package ftl

import ftl.cli.MainCommand
import ftl.run.exception.withGlobalExceptionHandling
import picocli.CommandLine

fun main(args: Array<String>) {
    withGlobalExceptionHandling {
        CommandLine(MainCommand()).execute(*args)
    }
}
