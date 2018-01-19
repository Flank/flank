package task

import com.sun.org.apache.xerces.internal.dom.DeferredElementImpl
import ftl.GlobalConfig
import ftl.TestRunner.testResultRgx
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths
import javax.xml.parsers.DocumentBuilderFactory

object MergeResults {

    fun execute() {
        val xmlFiles = mutableListOf<File>()
        val results = mutableMapOf<String, Int>()

        File(GlobalConfig.results).walk().forEach {
            if (it.name.matches(testResultRgx)) {
                xmlFiles.add(it)
            }
        }

        xmlFiles.forEach { file ->
            val xml = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(file)
            xml.normalizeDocument()
            xml.documentElement.normalize()

            val nodes = xml.getElementsByTagName("failure")

            repeat(nodes.length) { index ->
                val node: DeferredElementImpl = nodes.item(index) as DeferredElementImpl

                val parent = node.parentNode.attributes
                val className = parent.getNamedItem("classname").nodeValue
                val testName = parent.getNamedItem("name").nodeValue
                val key = "$className#$testName"
                results[key] = results.getOrDefault(key, 0) + 1
            }
        }

        val csv = Paths.get(GlobalConfig.results, "summary.csv")
        val csvData = StringBuilder()

        results.forEach {
            csvData.append(it.key, ',', it.value, "\n")
        }

        Files.write(csv, csvData.toString().toByteArray())
    }

    @JvmStatic
    fun main(args: Array<String>) {
       execute()
    }
}
