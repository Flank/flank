package utils

import com.fasterxml.jackson.databind.json.JsonMapper
import com.fasterxml.jackson.dataformat.xml.XmlMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.fasterxml.jackson.module.kotlin.readValue
import utils.testResults.TestSuites
import java.io.File

fun File.loadAsTestSuite(): TestSuites =
    XmlMapper().registerModule(KotlinModule()).readValue(this, TestSuites::class.java)

fun File.loadAndroidDumpShards() = jsonMapper
    .readValue<AndroidMatrixTestShards>(this).entries.first().value

fun File.loadIosDumpShards() = jsonMapper
    .readValue<IosMatrixTestShards>(this)

private val jsonMapper by lazy { JsonMapper().registerModule(KotlinModule()) }
