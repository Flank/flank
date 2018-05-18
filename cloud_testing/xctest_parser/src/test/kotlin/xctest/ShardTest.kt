package xctest

import com.dd.plist.NSArray
import com.dd.plist.NSDictionary
import com.dd.plist.PropertyListParser
import org.junit.Test
import java.io.File



class ShardTest {

    @Test
    fun shardWorks() {
        val xctestrun = File("./src/test/kotlin/xctest/fixtures/EarlGreyExampleSwiftTests_iphoneos11.3-arm64.xctestrun").canonicalFile
        if (!xctestrun.exists()) throw RuntimeException("$xctestrun doesn't exist")

        val root = PropertyListParser.parse(xctestrun) as NSDictionary

        // OnlyTestIdentifiers
        // Test-Class-Name[/Test-Method-Name]
        // Example/testExample
        val onlyTestIdentifiers = "OnlyTestIdentifiers"
        for (testTarget in root.allKeys()) {
            val testDictionary = (root[testTarget] as NSDictionary)

            val nsArray = NSArray(1)
            nsArray.setValue(0, "Example/testExample")

            testDictionary.remove(onlyTestIdentifiers)
            testDictionary["OnlyTestIdentifiers"] = nsArray
        }


        PropertyListParser.saveAsXML(root, xctestrun)
    }
}
