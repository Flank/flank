package flank.tool.resource

import java.io.BufferedReader
import java.io.InputStream

// app version: flank_snapshot
fun readVersion(): String = readTextResource("version.txt").trim()

// git commit name: 5b0d23215e3bd90e5f9c1c57149320634aad8008
fun readRevision(): String = readTextResource("revision.txt").trim()

fun readTextResource(name: String): String = getResource(name).bufferedReader().use(BufferedReader::readText)

fun getResource(name: String): InputStream = classLoader.getResourceAsStream(name)
    ?: throw Error("Unable to find resource: $name")

private val classLoader = Thread.currentThread().contextClassLoader
