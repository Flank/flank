package ftl.ios

import com.dd.plist.NSDictionary
import ftl.ios.xctest.findTestNames
import ftl.ios.xctest.parseToNSDictionary
import ftl.ios.xctest.rewriteXcTestRun
import java.io.File

typealias XctestrunMethods = Map<String, List<String>>

object Xctestrun {

    fun parse(xctestrun: String): NSDictionary = parseToNSDictionary(File(xctestrun))

    fun parse(xctestrun: ByteArray): NSDictionary = parseToNSDictionary(xctestrun)

    fun findTestNames(xctestrun: String): List<String> = findTestNames(File(xctestrun))

    fun rewrite(root: NSDictionary, methods: Collection<String>) = rewriteXcTestRun(root, methods)
}
