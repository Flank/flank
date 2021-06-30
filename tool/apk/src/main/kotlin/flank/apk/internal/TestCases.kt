package flank.apk.internal

import com.linkedin.dex.parser.DecodedValue
import com.linkedin.dex.parser.DexParser
import com.linkedin.dex.parser.TestAnnotation
import com.linkedin.dex.parser.TestMethod
import com.linkedin.dex.parser.formatClassName
import com.linkedin.dex.parser.getAnnotationsDirectory
import com.linkedin.dex.parser.getClassAnnotationValues
import com.linkedin.dex.spec.ClassDefItem
import com.linkedin.dex.spec.DexFile
import flank.apk.Apk

internal val parseApkTestCases = Apk.ParseTestCases { config ->
    fun TestMethod.plain(): Pair<String, List<String>> =
        testName to annotations.map(TestAnnotation::name)

    fun(path: String): List<String> {
        val testClasses = getTestClasses(path) { classDef ->
            isRunWith(classDef, config.filterClass)
        }

        val testMethods = DexParser.findTestMethods(path).run {
            val toDrop = testClasses.map(TestMethod::testName).toSet()
            filter { it.testName.split("#").first() !in toDrop }
        }


        return (testMethods + testClasses)
            .filter { method -> config.filterTest(method.plain()) }
            .map(TestMethod::testName)
    }
}

private fun getTestClasses(path: String, filter: DexFile.(ClassDefItem) -> Boolean): List<TestMethod> =
    DexParser.readDexFiles(path).fold(emptyList()) { accumulator, file: DexFile ->
        accumulator + file.run {
            classDefs.filter { item -> filter(item) }.map { item ->
                TestMethod(
                    testName = formatClassName(item).dropLast(1),
                    annotations = getClassAnnotationValues(getAnnotationsDirectory(item))
                )
            }
        }
    }

private fun DexFile.isRunWith(classDef: ClassDefItem, oneOfRunners: Set<String>): Boolean {
    // parse "Lorg/junit/runners/Parameterized;" to "org.junit.runners.Parameterized"
    fun DecodedValue.DecodedType.parsePackage() = value.drop(1).dropLast(1).replace("/", ".")

    return getClassAnnotationValues(getAnnotationsDirectory(classDef)).let { annotations: List<TestAnnotation> ->
        annotations.any { it.name.contains("RunWith", ignoreCase = true) } && annotations
            .flatMap { it.values.values }
            .filterIsInstance<DecodedValue.DecodedType>()
            .map { it.parsePackage() }
            .any { it in oneOfRunners }
    }
}
