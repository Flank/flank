package ftl.ios.xctest

import com.dd.plist.NSDictionary
import com.google.common.annotations.VisibleForTesting

fun rewriteXcTestRunV1(
    xctestrun: String,
    filterMethods: List<String>
): ByteArray =
    rewriteXcTestRunV1(
        parseToNSDictionary(xctestrun),
        findXcTestNamesV1(xctestrun).mapValues { (_, list) ->
            list.filter(filterMethods::contains)
        }
    )

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
