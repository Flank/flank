package ftl.ios.xctest

import com.dd.plist.NSArray
import com.dd.plist.NSDictionary
import com.dd.plist.NSObject
import ftl.run.exception.FlankConfigurationError

fun rewriteXcTestRunV2(
    xcTestPlan: String,
    filterMethods: List<String>
): Map<String, ByteArray> =
    rewriteXcTestRunV2(
        parseToNSDictionary(xcTestPlan),
        findXcTestNamesV1(xcTestPlan).mapValues { (_, list) ->
            list.filter(filterMethods::contains)
        },
    )

private fun rewriteXcTestRunV2(
    root: NSDictionary,
    methods: XctestrunMethods
): Map<String, ByteArray> =
    root.splitTestConfigurations().map { configDict ->
        val key = configDict.getName()
        key to root.clone().apply {
            put(
                TEST_CONFIGURATION,
                configDict.setOnlyTestIdentifiers(
                    methods[key] ?: emptyList()
                ).wrapInNSArray()
            )
        }.toByteArray()
    }.toMap()

private fun NSDictionary.splitTestConfigurations(): List<NSDictionary> =
    testConfigurationsNSArray().array.map { it as NSDictionary }

private fun NSDictionary.testConfigurationsNSArray(): NSArray =
    get(TEST_CONFIGURATION) as NSArray

private fun NSDictionary.getName(): String = get(NAME)
    ?.toJavaObject(String::class.java)
    ?: throw FlankConfigurationError("Cannot get Name key from NSDictionary:\n ${toXMLPropertyList()}")

private fun NSObject.wrapInNSArray() = NSArray().also { it.setValue(0, this) }
