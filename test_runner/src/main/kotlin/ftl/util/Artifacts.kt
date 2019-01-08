package ftl.util

import ftl.args.IArgs

object Artifacts {

    val testResultRgx = Regex(".*test_result_\\d+\\.xml$")

    fun regexList(args: IArgs): List<Regex> {
        return listOf(testResultRgx) + args.filesToDownload.map { Regex(it) }
    }
}
