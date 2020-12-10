package ftl.ios.xctest

import com.dd.plist.NSArray
import com.dd.plist.NSDictionary
import com.dd.plist.NSObject
import ftl.ios.xctest.common.TEST_CONFIGURATIONS
import ftl.ios.xctest.common.TEST_TARGETS
import ftl.ios.xctest.common.getBlueprintName
import ftl.ios.xctest.common.getTestConfigurations
import ftl.ios.xctest.common.getTestTargets
import ftl.ios.xctest.common.setOnlyTestIdentifiers

fun NSDictionary.reduceXcTestRunV2(
    configuration: String,
    targets: Map<String, List<String>>
): NSDictionary = apply {
    set(
        TEST_CONFIGURATIONS,
        getTestConfigurations()
            .getValue(configuration)
            .reduceTestConfiguration(targets)
            .inNSArray()
    )
}

private fun NSDictionary.reduceTestConfiguration(
    targetMethods: Map<String, List<String>>
): NSDictionary = apply {
    set(
        TEST_TARGETS,
        getTestTargets().mapNotNull { target ->
            targetMethods[target.getBlueprintName()]?.let { methods ->
                target.setOnlyTestIdentifiers(methods)
            }
        }.toNsArray()
    )
}

private fun <T : NSObject> List<T>.toNsArray(): NSArray =
    NSArray(size).also { nsArray ->
        forEachIndexed { index, t ->
            nsArray.setValue(index, t)
        }
    }

private fun NSObject.inNSArray() = NSArray(1).also { it.setValue(0, this) }
