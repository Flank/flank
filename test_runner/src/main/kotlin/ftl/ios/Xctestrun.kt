package ftl.ios

import com.dd.plist.NSDictionary
import ftl.ios.xctest.findTestNames
import ftl.ios.xctest.findTestsForTestTarget
import ftl.ios.xctest.parseToNSDictionary
import ftl.ios.xctest.rewriteXcTestRun
import java.io.File

typealias XctestrunMethods = Map<String, List<String>>

object Xctestrun {

    fun parse(xctestrun: String): NSDictionary = parseToNSDictionary(File(xctestrun))

    fun parse(xctestrun: ByteArray): NSDictionary = parseToNSDictionary(xctestrun)

    fun findTestNames(xctestrun: String): XctestrunMethods = findTestNames(File(xctestrun))

    fun findTestsForTarget(testTarget: String, xctestrun: String): List<String> = findTestsForTestTarget(testTarget, File(xctestrun))

    fun rewrite(xctestrun: String, methods: List<String>) = rewriteXcTestRun(xctestrun, methods)
}
