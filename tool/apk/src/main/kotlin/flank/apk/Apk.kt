package flank.apk

import flank.apk.internal.parseApkInfo
import flank.apk.internal.parseApkPackageName
import flank.apk.internal.parseApkTestCases

/**
 * Structured representation of the parsed test apk file.
 */
object Apk {

    class Api(
        val parseTestCases: ParseTestCases = parseApkTestCases,
        val parsePackageName: ParsePackageName = parseApkPackageName,
        val parseInfo: ParseInfo = parseApkInfo
    )

    data class Info(
        val packageName: String,
        val testRunner: String,
    )

    /**
     * @return The full list of test methods from parsed apk.
     */
    fun interface ParseTestCases : (LocalPath) -> TestCases

    /**
     * @return The [PackageName] for given apk.
     */
    fun interface ParsePackageName : (LocalPath) -> PackageName

    /**
     * @return The [Info] for given apk.
     */
    fun interface ParseInfo : (LocalPath) -> Info
}

/**
 * Local path to the test apk file.
 */
private typealias LocalPath = String

/**
 * The full package name for example: "example.full.package.name"
 */
private typealias PackageName = String

/**
 * List of test method names, where test method name is matching format: "package.name.ClassName#testCaseName"
 */
private typealias TestCases = List<String>
