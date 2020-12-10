package ftl.ios.xctest

import com.dd.plist.NSDictionary
import ftl.ios.xctest.common.XCTEST_METADATA
import ftl.ios.xctest.common.setOnlyTestIdentifiers

fun NSDictionary.reduceXcTestRunV1(
    targets: Map<String, List<String>>,
): NSDictionary = apply {
    (keys - targets.keys - XCTEST_METADATA).forEach(this::remove)
    targets.forEach { (testTarget, methods) ->
        setOnlyTestIdentifiers(testTarget, methods)
    }
}

private fun NSDictionary.setOnlyTestIdentifiers(
    testTarget: String,
    methods: List<String>
) {
    set(
        testTarget,
        getValue(testTarget)
            .let { it as NSDictionary }
            .setOnlyTestIdentifiers(methods)
    )
}
