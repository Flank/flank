package ftl.ios.xctest

import com.dd.plist.NSArray
import com.dd.plist.NSDictionary
import com.dd.plist.PropertyListParser
import ftl.run.exception.FlankGeneralError
import java.io.ByteArrayOutputStream
import java.io.File

typealias XctestrunMethods = Map<String, List<String>>

internal const val TEST_CONFIGURATION = "TestConfiguration"

internal const val NAME = "Name"

internal fun String.isMetadata(): Boolean = contentEquals("__xctestrun_metadata__")

internal fun String.isTestConfiguration(): Boolean = contentEquals(TEST_CONFIGURATION)

internal fun String.isTestPlan(): Boolean = contentEquals("TestPlan")

internal fun NSDictionary.toByteArray(): ByteArray {
    val out = ByteArrayOutputStream()
    PropertyListParser.saveAsXML(this, out)
    return out.toByteArray()
}

internal fun parseToNSDictionary(xctestrunPath: String): NSDictionary =
    parseToNSDictionary(File(xctestrunPath))

internal fun parseToNSDictionary(xctestrun: File): NSDictionary = xctestrun.canonicalFile
    .apply { if (!exists()) throw FlankGeneralError("$this doesn't exist") }
    .let(PropertyListParser::parse) as NSDictionary

internal fun parseToNSDictionary(xctestrun: ByteArray): NSDictionary =
    PropertyListParser.parse(xctestrun) as NSDictionary

internal fun parseXcMethodName(matcher: MatchResult): String = matcher.groupValues.last()
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

// Rewrites tests so that only the listed tests execute
fun NSDictionary.setOnlyTestIdentifiers(methods: Collection<String>) = apply {
    while (containsKey(ONLY_TEST_IDENTIFIERS)) remove(ONLY_TEST_IDENTIFIERS)
    this[ONLY_TEST_IDENTIFIERS] = NSArray(methods.size).also { methods.forEachIndexed(it::setValue) }
}

// https://github.com/google/xctestrunner/blob/51dbb6b7eb35f2ed55439459ca49e06992bc4da0/xctestrunner/test_runner/xctestrun.py#L129
private const val ONLY_TEST_IDENTIFIERS = "OnlyTestIdentifiers"
