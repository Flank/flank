package utils

import com.fasterxml.jackson.dataformat.xml.XmlMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import java.io.File
import utils.testResults.TestSuites

fun File.loadAsTestSuite(): TestSuites =
    XmlMapper().registerModule(KotlinModule()).readValue(this, TestSuites::class.java)
