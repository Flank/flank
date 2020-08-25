@file:JvmName("Utils")

package ftl.util

import ftl.run.exception.FlankGeneralError
import java.io.InputStream
import java.io.StringWriter
import java.nio.file.Files
import java.nio.file.Paths
import java.time.Instant
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.Random
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

fun String.trimStartLine(): String {
    return this.split("\n").drop(1).joinToString("\n")
}

fun StringWriter.println(msg: String = "") {
    this.append(msg + "\n")
}

fun String.write(data: String) {
    Files.write(Paths.get(this), data.toByteArray())
}

fun join(first: String, vararg more: String): String {
    // Note: Paths.get(...) does not work for joining because the path separator
    // will be '\' on Windows which is invalid for a URI
    return listOf(first, *more)
        .joinToString("/")
        .replace("\\", "/")
        .replace(regex = Regex("/+"), replacement = "/")
}

fun assertNotEmpty(str: String, e: String) {
    if (str.isEmpty()) {
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

private val userHome = System.getProperty("user.home")

fun copyBinaryResource(name: String) {
    val destinationPath = Paths.get(userHome, ".flank", name)
    val destinationFile = destinationPath.toFile()

    if (destinationFile.exists()) return
    destinationPath.parent.toFile().mkdirs()

    // "binaries/" folder prefix is required for Linux to find the resource.
    Files.copy(
        getResource("binaries/$name"),
        destinationPath
    )
    destinationFile.setExecutable(true)
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
