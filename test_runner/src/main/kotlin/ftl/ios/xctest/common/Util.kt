package ftl.ios.xctest.common

import com.dd.plist.NSArray
import com.dd.plist.NSDictionary
import com.dd.plist.PropertyListParser
import ftl.args.IosArgs
import ftl.run.exception.FlankConfigurationError
import ftl.run.exception.FlankGeneralError
import java.io.ByteArrayOutputStream
import java.io.File

typealias XctestrunMethods = Map<String, List<String>>

enum class XcTestRunVersion { V1, V2 }

internal const val XCTEST_METADATA = "__xctestrun_metadata__"
internal const val FORMAT_VERSION = "FormatVersion"
internal const val BUNDLE_ID = "TestHostBundleIdentifier"
internal const val TEST_CONFIGURATIONS = "TestConfigurations"
internal const val TEST_TARGETS = "TestTargets"
internal const val TEST_PLAN = "TestPlan"
internal const val NAME = "Name"
internal const val BLUEPRINT_NAME = "BlueprintName"
internal const val ONLY_TEST_IDENTIFIERS = "OnlyTestIdentifiers"

internal fun String.isMetadata(): Boolean = contentEquals(XCTEST_METADATA)

internal fun String.isTestPlan(): Boolean = contentEquals(TEST_PLAN)

internal fun NSDictionary.getXcTestRunVersion(): XcTestRunVersion =
    getXcTestRunVersionNumber().let { versionNumber ->
        XcTestRunVersion.values().getOrNull(versionNumber - 1)
            ?: throw FlankGeneralError("Unsupported xctestrun version $versionNumber")
    }

private fun NSDictionary.getXcTestRunVersionNumber(): Int =
    (get(XCTEST_METADATA) as? NSDictionary)
        ?.get(FORMAT_VERSION)?.toJavaObject(Int::class.java)
        ?: throw FlankGeneralError("Given NSDictionary doesn't contains $FORMAT_VERSION")

internal fun NSDictionary.getTestConfigurations(): Map<String, NSDictionary> =
    testConfigurationsNSArray().array.map { it as NSDictionary }.associateBy {
        it.getName()
    }

private fun NSDictionary.testConfigurationsNSArray(): NSArray =
    get(TEST_CONFIGURATIONS) as NSArray

internal fun NSDictionary.getTestTargets(): List<NSDictionary> =
    (get(TEST_TARGETS) as NSArray).array.map { it as NSDictionary }

internal fun NSDictionary.getOnlyTestIdentifiers() =
    (get(ONLY_TEST_IDENTIFIERS) as NSArray).array.map { it.toString() }

private fun NSDictionary.getName(): String = get(NAME)
    ?.toJavaObject(String::class.java)
    ?: throw FlankConfigurationError("Cannot get Name key from NSDictionary:\n ${toXMLPropertyList()}")

internal fun NSDictionary.getBlueprintName() = get(BLUEPRINT_NAME).toString()

internal fun IosArgs.getBundleId(): String =
    File(xctestrunFile).parentFile.walk().maxDepth(3).first { it.extension == "plist" && it.parent.endsWith(".app") }
        .let { plist ->
            PropertyListParser.parse(plist) as NSDictionary
        }["CFBundleIdentifier"]?.toString().orEmpty()

internal fun NSDictionary.toByteArray(): ByteArray {
    val out = ByteArrayOutputStream()
    PropertyListParser.saveAsXML(this, out)
    return out.toByteArray()
}

internal fun parseToNSDictionary(xctestrunPath: String): NSDictionary =
    parseToNSDictionary(File(xctestrunPath))

internal fun parseToNSDictionary(xctestrun: File): NSDictionary =
    xctestrun.canonicalFile
        .apply { if (!exists()) throw FlankGeneralError("$this doesn't exist") }
        .let(PropertyListParser::parse) as NSDictionary

internal fun parseToNSDictionary(xctestrun: ByteArray): NSDictionary =
    PropertyListParser.parse(xctestrun) as NSDictionary

internal fun parseXcMethodName(matcher: MatchResult): String =
    matcher.groupValues.last()
        .replace('.', '/')
        .replace(' ', '/')

internal fun String.quote() = "\"$this\""

internal fun validateIsFile(path: String) = File(path).run {
    when {
        !exists() -> throw FlankGeneralError("File $path does not exist!")
        isDirectory -> throw FlankGeneralError("$path is a directory!")
        else -> Unit
    }
}

// https://github.com/google/xctestrunner/blob/51dbb6b7eb35f2ed55439459ca49e06992bc4da0/xctestrunner/test_runner/xctestrun.py#L129
// Rewrites tests so that only the listed tests execute
internal fun NSDictionary.setOnlyTestIdentifiers(methods: Collection<String>) = apply {
    while (containsKey(ONLY_TEST_IDENTIFIERS)) remove(ONLY_TEST_IDENTIFIERS)
    this[ONLY_TEST_IDENTIFIERS] = NSArray(methods.size).also { methods.forEachIndexed(it::setValue) }
}

internal fun List<String>.mapToRegex(): List<Regex> = map { filter ->
    try {
        filter.toRegex()
    } catch (e: Exception) {
        throw FlankConfigurationError("Invalid regex: $filter", e)
    }
}
