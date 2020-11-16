package ftl.ios.xctest

import com.dd.plist.NSArray
import com.dd.plist.NSDictionary
import com.dd.plist.NSObject
import com.google.common.annotations.VisibleForTesting
import ftl.ios.xctest.common.TEST_CONFIGURATIONS
import ftl.ios.xctest.common.XctestrunMethods
import ftl.ios.xctest.common.getBlueprintName
import ftl.ios.xctest.common.getTestConfigurations
import ftl.ios.xctest.common.getTestTargets
import ftl.ios.xctest.common.parseToNSDictionary
import ftl.ios.xctest.common.setOnlyTestIdentifiers
import ftl.ios.xctest.common.toByteArray

fun rewriteXcTestRunV2(
    xcTestPlan: String,
    filterMethods: List<String> = emptyList()
): Map<String, ByteArray> =
    rewriteXcTestRunV2(
        parseToNSDictionary(xcTestPlan),
        findXcTestNamesV2(xcTestPlan).filterXcTestMethodsV2(filterMethods),
    )

@VisibleForTesting
internal fun Map<String, XctestrunMethods>.filterXcTestMethodsV2(
    names: List<String>
) = mapValues { (_, map) ->
    map.filterXcTestMethodsV1(names)
}

@VisibleForTesting
internal fun rewriteXcTestRunV2(
    root: NSDictionary,
    methods: Map<String, XctestrunMethods>
): Map<String, ByteArray> =
    root.getTestConfigurations().mapValues { (configName, configDict) ->
        root.clone().also { rootClone ->
            rootClone[TEST_CONFIGURATIONS] = configDict.clone().apply {
                getTestTargets().forEach { target ->
                    val testMethods = methods[configName]
                        ?.get(target.getBlueprintName())
                        ?: emptyList()
                    target.setOnlyTestIdentifiers(testMethods)
                }
            }.wrapInNSArray()
        }.toByteArray()
    }.toMap()

private fun NSObject.wrapInNSArray() = NSArray(1).also { it.setValue(0, this) }
