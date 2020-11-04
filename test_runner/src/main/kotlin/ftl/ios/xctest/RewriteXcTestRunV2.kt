package ftl.ios.xctest

import com.dd.plist.NSArray
import com.dd.plist.NSDictionary
import com.dd.plist.NSObject
import ftl.ios.xctest.common.TEST_CONFIGURATION
import ftl.ios.xctest.common.XctestrunMethods
import ftl.ios.xctest.common.getBlueprintName
import ftl.ios.xctest.common.getTestConfigurations
import ftl.ios.xctest.common.parseToNSDictionary
import ftl.ios.xctest.common.setOnlyTestIdentifiers
import ftl.ios.xctest.common.toByteArray

fun rewriteXcTestRunV2(
    xcTestPlan: String,
    filterMethods: List<String>
): Map<String, ByteArray> =
    rewriteXcTestRunV2(
        parseToNSDictionary(xcTestPlan),
        findXcTestNamesV2(xcTestPlan).mapValues { (_, map) ->
            map.mapValues { (_, list) ->
                list.filter(filterMethods::contains)
            }
        },
    )

private fun rewriteXcTestRunV2(
    root: NSDictionary,
    methods: Map<String, XctestrunMethods>
): Map<String, ByteArray> =
    root.getTestConfigurations().mapValues { (key, configDict) ->
        root.clone().also {
            val testMethods = methods[key]
                ?.get(configDict.getBlueprintName())
                ?: emptyList()

            it[TEST_CONFIGURATION] = configDict
                .setOnlyTestIdentifiers(testMethods)
                .wrapInNSArray()
        }.toByteArray()
    }.toMap()

private fun NSObject.wrapInNSArray() = NSArray().also { it.setValue(0, this) }
