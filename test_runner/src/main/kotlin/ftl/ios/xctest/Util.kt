package ftl.ios.xctest

import com.dd.plist.NSDictionary
import com.dd.plist.PropertyListParser
import ftl.run.exception.FlankGeneralError
import java.io.ByteArrayOutputStream
import java.io.File

internal fun String.isMetadata(): Boolean = contentEquals("__xctestrun_metadata__")

internal fun NSDictionary.toByteArray(): ByteArray {
    val out = ByteArrayOutputStream()
    PropertyListParser.saveAsXML(this, out)
    return out.toByteArray()
}

internal fun parseToNSDictionary(xctestrun: File): NSDictionary = xctestrun.canonicalFile
    .apply { if (!exists()) throw FlankGeneralError("$this doesn't exist") }
    .let(PropertyListParser::parse) as NSDictionary

internal fun parseToNSDictionary(xctestrun: ByteArray): NSDictionary =
    PropertyListParser.parse(xctestrun) as NSDictionary
