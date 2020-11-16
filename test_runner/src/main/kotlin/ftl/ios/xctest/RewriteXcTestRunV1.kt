package ftl.ios.xctest

import com.dd.plist.NSDictionary
import com.google.common.annotations.VisibleForTesting
import ftl.ios.xctest.common.XctestrunMethods
import ftl.ios.xctest.common.isMetadata
import ftl.ios.xctest.common.parseToNSDictionary
import ftl.ios.xctest.common.setOnlyTestIdentifiers
import ftl.ios.xctest.common.toByteArray

fun rewriteXcTestRunV1(
    xctestrun: String,
    filterMethods: List<String>
): ByteArray =
    rewriteXcTestRunV1(
        parseToNSDictionary(xctestrun),
        findXcTestNamesV1(xctestrun).filterXcTestMethodsV1(filterMethods)
    )

fun XctestrunMethods.filterXcTestMethodsV1(
    names: List<String>
) = mapValues { (_, list) ->
    list.filter(names::contains)
}

@VisibleForTesting
internal fun rewriteXcTestRunV1(
    root: NSDictionary,
    methods: XctestrunMethods
): ByteArray =
    root.clone().apply {
        allKeys().filterNot(String::isMetadata).map { testTarget ->
            Pair(
                get(testTarget) as NSDictionary,
                methods[testTarget] ?: emptyList()
            )
        }.forEach { (dict, methods) ->
            dict.setOnlyTestIdentifiers(methods)
        }
    }.toByteArray()
