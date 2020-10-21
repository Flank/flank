package ftl.ios.xctest

import com.dd.plist.NSArray
import com.dd.plist.NSDictionary

fun rewriteXcTestRun(
    root: NSDictionary,
    methods: Collection<String>
): ByteArray = root.clone().apply {
    allKeys().filterNot(String::isMetadata).forEach { testTarget ->
        (get(testTarget) as NSDictionary).setOnlyTestIdentifiers(methods)
    }
}.toByteArray()

// Rewrites tests so that only the listed tests execute
private fun NSDictionary.setOnlyTestIdentifiers(tests: Collection<String>) {
    while (containsKey(ONLY_TEST_IDENTIFIERS)) remove(ONLY_TEST_IDENTIFIERS)
    this[ONLY_TEST_IDENTIFIERS] = NSArray(tests.size).also { tests.forEachIndexed(it::setValue) }
}

// https://github.com/google/xctestrunner/blob/51dbb6b7eb35f2ed55439459ca49e06992bc4da0/xctestrunner/test_runner/xctestrun.py#L129
private const val ONLY_TEST_IDENTIFIERS = "OnlyTestIdentifiers"
