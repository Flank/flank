package flank.filter

import flank.filter.internal.fromTestTargets
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.nio.file.Path
import java.nio.file.Paths

val FOO_PACKAGE: Target = "foo.ClassName#testName" to emptyList()
val BAR_PACKAGE: Target = "bar.ClassName#testName" to emptyList()
val FOO_CLASSNAME: Target = "whatever.Foo#testName" to emptyList()
val BAR_CLASSNAME: Target = "whatever.Bar#testName" to emptyList()
val WITHOUT_IGNORE_ANNOTATION: Target = "whatever.Foo#testName" to emptyList()
val WITH_IGNORE_ANNOTATION: Target = "whatever.Foo#testName" to listOf("org.junit.Ignore")
val WITH_FOO_ANNOTATION: Target = "whatever.Foo#testName" to listOf("Foo")
val WITH_BAR_ANNOTATION: Target = "whatever.Foo#testName" to listOf("Bar")
val WITHOUT_FOO_ANNOTATION: Target = "whatever.Foo#testName" to emptyList()
val WITH_FOO_ANNOTATION_AND_PACKAGE: Target = "foo.Bar#testName" to listOf("Foo")
val WITH_LARGE_ANNOTATION: Target = "whatever.Foo#testName" to listOf("android.test.suitebuilder.annotation.LargeTest")
val WITH_MEDIUM_ANNOTATION: Target = "whatever.Foo#testName" to listOf("android.support.test.filters.MediumTest")
val WITH_SMALL_ANNOTATION: Target = "whatever.Foo#testName" to listOf("androidx.test.filters.SmallTest")
val WITHOUT_LARGE_ANNOTATION: Target = "whatever.Foo#testName" to emptyList()
val WITHOUT_MEDIUM_ANNOTATION: Target = "whatever.Foo#testName" to emptyList()
val WITHOUT_SMALL_ANNOTATION: Target = "whatever.Foo#testName" to emptyList()
const val TEST_FILE = "src/test/resources/dummy-tests-file.txt"
const val TEST_FILE_2 = "src/test/resources/exclude-tests.txt"
private const val IGNORE_ANNOTATION = "org.junit.Ignore"

@Suppress("TooManyFunctions")
class FilterKtTest {

    private val targets = listOf(
        TargetsHelper(
            pack = "anyPackage_1",
            cl = "anyClass_1",
            m = "anyMethod_1",
            annotation = IGNORE_ANNOTATION
        ),
        TargetsHelper(pack = "anyPackage_2", cl = "anyClass_2", m = "anyMethod_2", annotation = "Foo"),
        TargetsHelper(pack = "anyPackage_3", cl = "anyClass_3", m = "anyMethod_3", annotation = "Bar"),
        TargetsHelper(
            pack = "anyPackage_4",
            cl = "anyClass_4",
            m = "anyMethod_4",
            annotation = IGNORE_ANNOTATION
        )
    )

    private val testMethodSet = targets.map { getDefaultTestMethod(it.fullView, it.annotation) }
    private val commonExpected = targets.map { it.fullView }

    @Test
    fun testIgnoreMultipleAnnotations() {
        val target: Target = "com.example.app.ExampleUiTest#testFails" to listOf(
            "org.junit.runner.RunWith",
            "org.junit.Ignore",
            "org.junit.Test",
        )

        val filter = createTestCasesFilter(listOf("notAnnotation org.junit.Ignore"))

        assertFalse(filter(target))
    }

    @Test
    fun testFilteringByPackage() {
        val filter = createTestCasesFilter(listOf("package foo"))

        assertTrue(filter(FOO_PACKAGE))
        assertFalse(filter(BAR_PACKAGE))
    }

    @Test
    fun testFilteringByPackageNegative() {
        val filter = createTestCasesFilter(listOf("notPackage foo"))

        assertFalse(filter(FOO_PACKAGE))
        assertTrue(filter(BAR_PACKAGE))
    }

    @Test
    fun testFilteringByClassName() {
        val filter = createTestCasesFilter(listOf("class whatever.Foo"))

        assertTrue(filter(FOO_CLASSNAME))
        assertFalse(filter(BAR_CLASSNAME))
    }

    @Test
    fun testFilteringByClassNameNegative() {
        val filter = createTestCasesFilter(listOf("notClass whatever.Foo"))

        assertFalse(filter(FOO_CLASSNAME))
        assertTrue(filter(BAR_CLASSNAME))
    }

    @Test
    fun `empty targets should not filter @Ignore annotated tests`() {
        val filter = createTestCasesFilter(listOf())

        assertTrue(filter(WITH_IGNORE_ANNOTATION))
        assertTrue(filter(WITHOUT_IGNORE_ANNOTATION))
    }

    @Test
    fun testFilteringByAnnotation() {
        val filter = createTestCasesFilter(listOf("annotation Foo,Bar"))

        assertTrue(filter(WITH_FOO_ANNOTATION))
        assertTrue(filter(WITH_BAR_ANNOTATION))
        assertFalse(filter(WITHOUT_FOO_ANNOTATION))
    }

    @Test
    fun testFilteringByAnnotationWithSpaces() {
        val filter = createTestCasesFilter(listOf("annotation Foo, Bar"))

        assertTrue(filter(WITH_FOO_ANNOTATION))
        assertTrue(filter(WITH_BAR_ANNOTATION))
        assertFalse(filter(WITHOUT_FOO_ANNOTATION))
    }

    @Test
    fun testFilteringBySizeLarge() {
        val filter = createTestCasesFilter(listOf("size large"))

        assertTrue(filter(WITH_LARGE_ANNOTATION))
        assertFalse(filter(WITHOUT_LARGE_ANNOTATION))
    }

    @Test
    fun testFilteringBySizeMedium() {
        val filter = createTestCasesFilter(listOf("size medium"))

        assertTrue(filter(WITH_MEDIUM_ANNOTATION))
        assertFalse(filter(WITHOUT_MEDIUM_ANNOTATION))
    }

    @Test
    fun testFilteringBySizeSmall() {
        val filter = createTestCasesFilter(listOf("size small"))

        assertTrue(filter(WITH_SMALL_ANNOTATION))
        assertFalse(filter(WITHOUT_SMALL_ANNOTATION))
    }

    @Test(expected = IllegalArgumentException::class)
    fun testFilteringBySizeInvalidWillThrowException() {
        createTestCasesFilter(listOf("size foo"))
    }

    @Test
    fun testFilteringBySizes() {
        val filter = createTestCasesFilter(listOf("size large,small"))

        assertTrue(filter(WITH_LARGE_ANNOTATION))
        assertTrue(filter(WITH_SMALL_ANNOTATION))
        assertFalse(filter(WITHOUT_LARGE_ANNOTATION))
        assertFalse(filter(WITHOUT_SMALL_ANNOTATION))
    }

    @Test
    fun testFilteringBySizesWithSpace() {
        val filter = createTestCasesFilter(listOf("size large, small"))

        assertTrue(filter(WITH_LARGE_ANNOTATION))
        assertTrue(filter(WITH_SMALL_ANNOTATION))
        assertFalse(filter(WITHOUT_LARGE_ANNOTATION))
        assertFalse(filter(WITHOUT_SMALL_ANNOTATION))
    }

    @Test(expected = IllegalArgumentException::class)
    fun testFilteringByNotSizesWillThrowException() {
        createTestCasesFilter(listOf("notSize large"))
    }

    @Test
    fun testFilteringByAnnotationNegative() {
        val filter = createTestCasesFilter(listOf("notAnnotation Foo"))

        assertFalse(filter(WITH_FOO_ANNOTATION))
        assertTrue(filter(WITHOUT_FOO_ANNOTATION))
    }

    @Test
    fun allOfProperlyChecksAllFilters() {
        val filter = createTestCasesFilter(listOf("package foo,bar", "annotation Foo"))

        assertFalse(filter(FOO_PACKAGE))
        assertFalse(filter(BAR_PACKAGE))
        assertFalse(filter(WITH_FOO_ANNOTATION))
        assertTrue(filter(WITH_FOO_ANNOTATION_AND_PACKAGE))
    }

    @Test
    fun testFilteringFromFileNegative() {
        val file = getPath(TEST_FILE)
        val filePath = file.toString()

        val filter = createTestCasesFilter(listOf("testFile $filePath"))

        assertTrue(filter(FOO_PACKAGE))
        assertTrue(filter(BAR_PACKAGE))
    }

    @Test
    fun testFilteringFromFile() {
        val file = getPath(TEST_FILE)
        val filePath = file.toString()

        val filter = createTestCasesFilter(listOf("notTestFile $filePath"))

        assertFalse(filter(FOO_PACKAGE))
        assertFalse(filter(BAR_PACKAGE))
    }

    @Test(expected = IllegalArgumentException::class)
    fun passingMalformedCommandWillThrowException() {
        createTestCasesFilter(listOf("class=com.my.package"))
    }

    @Test(expected = IllegalArgumentException::class)
    fun passingInvalidCommandWillThrowException() {
        createTestCasesFilter(listOf("invalidCommand com.my.package"))
    }

    @Test
    fun classFilterOverridesNotAnnotation() {
        val filter = fromTestTargets(
            listOf(
                "notAnnotation Foo",
                "class anyPackage_2.anyClass_2#anyMethod_2",
                "class anyPackage_3.anyClass_3#anyMethod_3"
            )
        )

        val output = mutableListOf<String>()
        val filtered = testMethodSet.asSequence().filter { test ->
            val result = filter.shouldRun(test)
            output.add("""$result ${test.first} [${filter.describe}]""")
            result
        }.map { "class ${it.first}" }.toList()

        val expected = listOf(
            "false anyPackage_1.anyClass_1#anyMethod_1 [allOf [not (withAnnotation (Foo)), anyOf [withClassName (anyPackage_2.anyClass_2#anyMethod_2), withClassName (anyPackage_3.anyClass_3#anyMethod_3)]]]",
            "false anyPackage_2.anyClass_2#anyMethod_2 [allOf [not (withAnnotation (Foo)), anyOf [withClassName (anyPackage_2.anyClass_2#anyMethod_2), withClassName (anyPackage_3.anyClass_3#anyMethod_3)]]]",
            "true anyPackage_3.anyClass_3#anyMethod_3 [allOf [not (withAnnotation (Foo)), anyOf [withClassName (anyPackage_2.anyClass_2#anyMethod_2), withClassName (anyPackage_3.anyClass_3#anyMethod_3)]]]",
            "false anyPackage_4.anyClass_4#anyMethod_4 [allOf [not (withAnnotation (Foo)), anyOf [withClassName (anyPackage_2.anyClass_2#anyMethod_2), withClassName (anyPackage_3.anyClass_3#anyMethod_3)]]]"
        )

        assertEquals(expected, output)
        assertEquals(listOf("class anyPackage_3.anyClass_3#anyMethod_3"), filtered)
    }

    @Test
    fun notAnnotationFiltersWithClass() {
        val filter = createTestCasesFilter(listOf("notAnnotation Foo", "class anyPackage_1.anyClass_1#anyMethod_1"))
        val filtered = testMethodSet.withFilter(filter)

        assertEquals(listOf("anyPackage_1.anyClass_1#anyMethod_1"), filtered)
    }

    @Test
    fun notAnnotationFilters() {
        val filter = createTestCasesFilter(listOf("notAnnotation Moo"))
        val filtered = testMethodSet.withFilter(filter, enrich = false)

        assertEquals(commonExpected, filtered)
    }

    @Test
    fun methodOverrideIgnored() {
        val filter = createTestCasesFilter(targets.map { it.methodView })
        val filtered = testMethodSet.withFilter(filter)

        assertEquals(commonExpected, filtered)
    }

    @Test
    fun multipleClassesResolveToMethods() {
        val filter = createTestCasesFilter(targets.map { it.classView })
        val filtered = testMethodSet.withFilter(filter)

        assertEquals(commonExpected, filtered)
    }

    @Test
    fun multiplePackagesResolveToMethods() {
        val filter = createTestCasesFilter(targets.map { it.packageView })
        val filtered = testMethodSet.withFilter(filter)

        assertEquals(commonExpected, filtered)
    }

    @Test
    fun `by default - should contain method annotated with @Ignore`() {
        val byPackageFilter = createTestCasesFilter(targets.map { it.packageView })
        val byClassFilter = createTestCasesFilter(targets.map { it.classView })
        val byNotAnnotationFilter = createTestCasesFilter(listOf("notAnnotation ThereIsNoSuchAnnotation"))

        val byPackage = testMethodSet.withFilter(byPackageFilter)
        val byClass = testMethodSet.withFilter(byClassFilter)
        val byNotAnnotation = testMethodSet.withFilter(byNotAnnotationFilter, enrich = false)

        assertEquals(commonExpected, byPackage)
        assertEquals(commonExpected, byClass)
        assertEquals(commonExpected, byNotAnnotation)
    }

    @Test
    fun `should filter tests annotated with @Ignore if user explicitly want to do so`() {
        val byNotAnnotationFilter = createTestCasesFilter(listOf("notAnnotation $IGNORE_ANNOTATION"))
        val byNotAnnotation = testMethodSet.withFilter(byNotAnnotationFilter, enrich = false)
        val expected = targets.filterNot { it.annotation == IGNORE_ANNOTATION }.map { it.fullView }

        assertEquals(byNotAnnotation, expected)
    }

    @Test
    fun testFilteringClassAndPackageNegative() {
        val filter = createTestCasesFilter(listOf("notPackage foo", "notClass whatever.Bar"))

        assertFalse(filter(FOO_PACKAGE))
        assertFalse(filter(BAR_CLASSNAME))
        assertTrue(filter(BAR_PACKAGE))
    }

    @Test
    fun testFilteringClassAndPackageNegativeFromFile() {
        val file = getPath(TEST_FILE_2) // contents: foo whatever.Bar
        val filePath = file.toString()

        val filter = createTestCasesFilter(listOf("notTestFile $filePath"))

        assertFalse(filter(FOO_PACKAGE))
        assertFalse(filter(BAR_CLASSNAME))
        assertTrue(filter(BAR_PACKAGE))
    }

    @Test
    fun `inclusion filter should override exclusion filter`() {
        val filter = createTestCasesFilter(listOf("notPackage foo", "class whatever.Bar"))

        assertFalse(filter(FOO_PACKAGE))
        assertFalse(filter(BAR_PACKAGE))
        assertTrue(filter(BAR_CLASSNAME))
    }

    @Test
    fun `withClassName should correctly filter classes with similar name`() {
        // test-targets:
        //   - class foo.bar.Class1
        // should filter foo.bar.Class11, foo.bar.Class101 ...
        // the same is applicable for methods

        val filter = createTestCasesFilter(
            listOf(
                "class anyPackage_1.anyClass_1",
                "class anyPackage_3.anyClass_3#anyMethod_3"
            )
        )

        val tests = listOf(
            TargetsHelper(pack = "anyPackage_1", cl = "anyClass_1", m = "anyMethod_1", annotation = "Foo"),
            TargetsHelper(pack = "anyPackage_1", cl = "anyClass_1", m = "anyMethod_2", annotation = "Foo"),
            TargetsHelper(pack = "anyPackage_1", cl = "anyClass_12", m = "anyMethod_1", annotation = "Bar"),
            TargetsHelper(pack = "anyPackage_1", cl = "anyClass_12", m = "anyMethod_12", annotation = "Bar"),
            TargetsHelper(pack = "anyPackage_3", cl = "anyClass_3", m = "anyMethod_3", annotation = "Bar"),
            TargetsHelper(pack = "anyPackage_3", cl = "anyClass_3", m = "anyMethod_32", annotation = "Bar"),
            TargetsHelper(pack = "anyPackage_3", cl = "anyClass_32", m = "anyMethod_3", annotation = "Bar"),
            TargetsHelper(pack = "anyPackage_3", cl = "anyClass_32", m = "anyMethod_32", annotation = "Bar")
        ).map { getDefaultTestMethod(it.fullView, it.annotation) }

        val expected = listOf(
            "anyPackage_1.anyClass_1#anyMethod_1",
            "anyPackage_1.anyClass_1#anyMethod_2",
            "anyPackage_3.anyClass_3#anyMethod_3"
        )

        val result = tests.withFilter(filter)

        assertEquals(expected, result)
    }
}

private fun getPath(path: String): Path =
    Paths.get(path).toAbsolutePath().normalize()

private fun getDefaultTestMethod(testName: String, annotation: String): Target =
    testName to listOf(annotation)

private fun List<Target>.add(method: Target) = listOf(*this.toTypedArray() + method)

private fun List<Target>.withFilter(shouldRun: ShouldRun, enrich: Boolean = true) =
    if (enrich) add(getDefaultTestMethod("should.be#filtered", "AnyAnnotation"))
        .filter(shouldRun).map { it.first }
    else this
        .filter(shouldRun).map { it.first }

private class TargetsHelper(
    private val pack: String,
    private val cl: String,
    private val m: String,
    val annotation: String
) {
    val classView: String
        get() = "class $pack.$cl"

    val packageView: String
        get() = "package $pack"

    val methodView: String
        get() = "class $fullView"

    val fullView: String
        get() = "$pack.$cl#$m"
}
