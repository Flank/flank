package utils

import com.fasterxml.jackson.dataformat.xml.XmlMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import utils.testResults.TestSuites
import java.io.File

fun File.loadAsTestSuite(): TestSuites =
    XmlMapper().registerModule(KotlinModule()).readValue(this, TestSuites::class.java)
