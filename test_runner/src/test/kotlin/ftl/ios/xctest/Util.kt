package ftl.ios.xctest

import com.google.common.truth.Truth.assertThat

const val FIXTURES_PATH = "./src/test/kotlin/ftl/fixtures/tmp"

const val swiftXcTestRunV1 = "$FIXTURES_PATH/ios/EarlGreyExample/EarlGreyExampleSwiftTests.xctestrun"
const val multiTargetsSwiftXcTestRunV1 = "$FIXTURES_PATH/ios/FlankExample/FlankExample.xctestrun"
const val swiftXcTestRunV2 = "$FIXTURES_PATH/ios/multi_test_targets/AllTests_AllTests_iphoneos13.7-arm64e.xctestrun"

const val objcBinary = "$FIXTURES_PATH/ios/EarlGreyExample/EarlGreyExampleTests"
const val swiftBinary = "$FIXTURES_PATH/ios/EarlGreyExample/EarlGreyExampleSwiftTests"

val objcTestsV1 = listOf(
    "EarlGreyExampleTests/testBasicSelection",
    "EarlGreyExampleTests/testBasicSelectionActionAssert",
    "EarlGreyExampleTests/testBasicSelectionAndAction",
    "EarlGreyExampleTests/testBasicSelectionAndAssert",
    "EarlGreyExampleTests/testCatchErrorOnFailure",
    "EarlGreyExampleTests/testCollectionMatchers",
    "EarlGreyExampleTests/testCustomAction",
    "EarlGreyExampleTests/testLayout",
    "EarlGreyExampleTests/testSelectionOnMultipleElements",
    "EarlGreyExampleTests/testTableCellOutOfScreen",
    "EarlGreyExampleTests/testWithCondition",
    "EarlGreyExampleTests/testWithCustomAssertion",
    "EarlGreyExampleTests/testWithCustomFailureHandler",
    "EarlGreyExampleTests/testWithCustomMatcher",
    "EarlGreyExampleTests/testWithInRoot"
)

val swiftTestsV1 = listOf(
    "EarlGreyExampleSwiftTests/testBasicSelection",
    "EarlGreyExampleSwiftTests/testBasicSelectionActionAssert",
    "EarlGreyExampleSwiftTests/testBasicSelectionAndAction",
    "EarlGreyExampleSwiftTests/testBasicSelectionAndAssert",
    "EarlGreyExampleSwiftTests/testCatchErrorOnFailure",
    "EarlGreyExampleSwiftTests/testCollectionMatchers",
    "EarlGreyExampleSwiftTests/testCustomAction",
    "EarlGreyExampleSwiftTests/testLayout",
    "EarlGreyExampleSwiftTests/testSelectionOnMultipleElements",
    "EarlGreyExampleSwiftTests/testTableCellOutOfScreen",
    "EarlGreyExampleSwiftTests/testThatThrows",
    "EarlGreyExampleSwiftTests/testWithCondition",
    "EarlGreyExampleSwiftTests/testWithCustomAssertion",
    "EarlGreyExampleSwiftTests/testWithCustomFailureHandler",
    "EarlGreyExampleSwiftTests/testWithCustomMatcher",
    "EarlGreyExampleSwiftTests/testWithGreyAssertions",
    "EarlGreyExampleSwiftTests/testWithInRoot"
)

val swiftTestsV2 = mapOf(
    "en" to mapOf(
        "UITests" to listOf(
            "UITests/test1",
            "UITests/test1ENLocale",
            "UITests/test1PLLocale",
            "UITests/test3",
        ),
        "SecondUITests" to listOf(
            "SecondUITests/test11",
            "SecondUITests/test12",
            "SecondUITests/test13",
            "SecondUITests/test2ENLocale",
            "SecondUITests/test2PLLocale",
        )
    ),
    "pl" to mapOf(
        "UITests" to listOf(
            "UITests/test1",
            "UITests/test1ENLocale",
            "UITests/test1PLLocale",
            "UITests/test3",
        ),
        "SecondUITests" to listOf(
            "SecondUITests/test11",
            "SecondUITests/test12",
            "SecondUITests/test13",
            "SecondUITests/test2ENLocale",
            "SecondUITests/test2PLLocale",
        )
    )
)

fun checkObjcTests(actual: List<String>) {
    actual.forEachIndexed { index, result ->
        assertThat(objcTestsV1[index]).isEqualTo(result)
    }

    assertThat(objcTestsV1.size).isEqualTo(actual.size)
}

fun checkSwiftTests(actual: List<String>) {
    actual.forEachIndexed { index, result ->
        assertThat(result).isEqualTo(swiftTestsV1[index])
    }

    assertThat(actual.size).isEqualTo(swiftTestsV1.size)
}
