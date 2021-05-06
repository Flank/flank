@file:JvmName("Utils")

package ftl.util

import com.fasterxml.jackson.annotation.JsonProperty
import flank.common.logLn
import ftl.run.exception.FlankGeneralError
import java.io.File
import java.io.InputStream
import java.time.Instant
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.Random
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KMutableProperty
import kotlin.reflect.KProperty

fun assertNotEmpty(str: String, e: String) {
    if (str.isBlank()) {
        throw FlankGeneralError(e)
    }
}

// Match _GenerateUniqueGcsObjectName from api_lib/firebase/test/arg_validate.py
//
// Example: 2017-05-31_17:19:36.431540_hRJD
//
// https://cloud.google.com/storage/docs/naming
fun uniqueObjectName(): String {
    val bucketName = StringBuilder()
    val instant = Instant.now()

    bucketName.append(
        DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss.")
            .withZone(ZoneOffset.UTC)
            .format(instant)
    )

    val nanoseconds = instant.nano.toString()

    if (nanoseconds.length >= 6) {
        bucketName.append(nanoseconds.substring(0, 6))
    } else {
        bucketName.append(nanoseconds.substring(0, nanoseconds.length - 1))
    }

    bucketName.append("_")

    val random = Random()
    // a-z: 97 - 122
    // A-Z: 65 - 90
    repeat(4) {
        val ascii = random.nextInt(26)
        var letter = (ascii + 'a'.toInt()).toChar()

        if (ascii % 2 == 0) {
            letter -= 32 // upcase
        }

        bucketName.append(letter)
    }

    return bucketName.toString()
}

private val classLoader = Thread.currentThread().contextClassLoader

private fun getResource(name: String): InputStream {
    return classLoader.getResourceAsStream(name)
        ?: throw FlankGeneralError("Unable to find resource: $name")
}

fun printVersionInfo() {
    logLn("version: ${readVersion()}")
    logLn("revision: ${readRevision()}")
    logLn("session id: $sessionId")
    logLn()
}

// app version: flank_snapshot
fun readVersion(): String {
    return readTextResource("version.txt").trim()
}

// git commit name: 5b0d23215e3bd90e5f9c1c57149320634aad8008
fun readRevision(): String {
    return readTextResource("revision.txt").trim()
}

fun readTextResource(name: String): String {
    return getResource(name).bufferedReader().use { it.readText() }
}

fun <R : MutableMap<String, Any>, T> mutableMapProperty(
    name: String? = null,
    defaultValue: () -> T
) = object : ReadWriteProperty<R, T> {
    @Suppress("UNCHECKED_CAST")
    override fun getValue(thisRef: R, property: KProperty<*>): T =
        thisRef.getOrElse(name ?: property.name, defaultValue) as T

    override fun setValue(thisRef: R, property: KProperty<*>, value: T) =
        thisRef.set(name ?: property.name, value as Any)
}

/**
 * Strips all characters except numbers and a period
 * Returns 0 when the string is null or blank
 *
 * Example: z1,23.45 => 123.45 */
fun String?.stripNotNumbers(): String {
    if (this.isNullOrBlank()) return "0"
    return this.replace(Regex("""[^0-9\\.]"""), "")
}

/**
 * Used to validate values from yml config file.
 * Should be used only on properties with [JsonProperty] annotation.
 */
fun <T> KMutableProperty<T?>.require() =
    getter.call() ?: throw FlankGeneralError(
        "Invalid value for [${
        setter.annotations.filterIsInstance<JsonProperty>().first().value
        }]: no argument value found"
    )

fun getGACPathOrEmpty(): String = System.getenv("GOOGLE_APPLICATION_CREDENTIALS").orEmpty()

fun saveToFlankLinks(vararg links: String) = File("flank-links.log").writeText(links.joinToString(System.lineSeparator()))
