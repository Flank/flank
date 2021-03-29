package ftl.util

import picocli.CommandLine

interface PrintHelp : Runnable {
    override fun run() {
        CommandLine.usage(this, System.out)
    }
}
