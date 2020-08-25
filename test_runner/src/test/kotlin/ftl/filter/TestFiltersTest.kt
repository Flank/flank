package ftl.filter

import com.google.common.truth.Truth.assertThat
import com.linkedin.dex.parser.TestAnnotation
import com.linkedin.dex.parser.TestMethod
import ftl.filter.TestFilters.fromTestTargets
import ftl.test.util.FlankTestRunner
import ftl.test.util.TestHelper
import ftl.run.exception.FlankConfigurationError
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith

val FOO_PACKAGE = TestMethod("foo.ClassName#testName", emptyList())
val BAR_PACKAGE = TestMethod("bar.ClassName#testName", emptyList())
val FOO_CLASSNAME = TestMethod("whatever.Foo#testName", emptyList())
val BAR_CLASSNAME = TestMethod("whatever.Bar#testName", emptyList())
val WITHOUT_IGNORE_ANNOTATION = TestMethod("whatever.Foo#testName", emptyList())
val WITH_IGNORE_ANNOTATION =
    TestMethod("whatever.Foo#testName", listOf(TestAnnotation("org.junit.Ignore", emptyMap(), false)))
val WITH_FOO_ANNOTATION = TestMethod("whatever.Foo#testName", listOf(TestAnnotation("Foo", emptyMap(), false)))
val WITH_BAR_ANNOTATION = TestMethod("whatever.Foo#testName", listOf(TestAnnotation("Bar", emptyMap(), false)))
val WITHOUT_FOO_ANNOTATION = TestMethod("whatever.Foo#testName", emptyList())
val WITH_FOO_ANNOTATION_AND_PACKAGE = TestMethod("foo.Bar#testName", listOf(TestAnnotation("Foo", emptyMap(), false)))
val WITH_LARGE_ANNOTATION =
    TestMethod("whatever.Foo#testName", listOf(TestAnnotation("android.test.suitebuilder.annotation.LargeTest", emptyMap(), false)))
val WITH_MEDIUM_ANNOTATION =
    TestMethod("whatever.Foo#testName", listOf(TestAnnotation("android.support.test.filters.MediumTest", emptyMap(), false)))
val WITH_SMALL_ANNOTATION =
    TestMethod("whatever.Foo#testName", listOf(TestAnnotation("androidx.test.filters.SmallTest", emptyMap(), false)))
val WITHOUT_LARGE_ANNOTATION = TestMethod("whatever.Foo#testName", emptyList())
val WITHOUT_MEDIUM_ANNOTATION = TestMethod("whatever.Foo#testName", emptyList())
val WITHOUT_SMALL_ANNOTATION = TestMethod("whatever.Foo#testName", emptyList())
const val TEST_FILE = "src/test/kotlin/ftl/filter/fixtures/dummy-tests-file.txt"
const val TEST_FILE_2 = "src/test/kotlin/ftl/filter/fixtures/exclude-tests.txt"
private const val IGNORE_ANNOTATION = "org.junit.Ignore"

@Suppress("TooManyFunctions")
@RunWith(FlankTestRunner::class)
class TestFiltersTest {

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
        val m1 = TestMethod(
            "com.example.app.ExampleUiTest#testFails", listOf(
                TestAnnotation("org.junit.runner.RunWith", emptyMap(), false),
                TestAnnotation("org.junit.Ignore", emptyMap(), false),
                TestAnnotation("org.junit.Test", emptyMap(), false)
            )
        )

        val filter = fromTestTargets(listOf("notAnnotation org.junit.Ignore"))

        assertThat(filter.shouldRun(m1)).isFalse()
    }

    @Test
    fun testFilteringByPackage() {
        val filter = fromTestTargets(listOf("package foo"))

        assertThat(filter.shouldRun(FOO_PACKAGE)).isTrue()
        assertThat(filter.shouldRun(BAR_PACKAGE)).isFalse()
    }

    @Test
    fun testFilteringByPackageNegative() {
        val filter = fromTestTargets(listOf("notPackage foo"))

        assertThat(filter.shouldRun(FOO_PACKAGE)).isFalse()
        assertThat(filter.shouldRun(BAR_PACKAGE)).isTrue()
    }

    @Test
    fun testFilteringByClassName() {
        val filter = fromTestTargets(listOf("class whatever.Foo"))

        assertThat(filter.shouldRun(FOO_CLASSNAME)).isTrue()
        assertThat(filter.shouldRun(BAR_CLASSNAME)).isFalse()
    }

    @Test
    fun testFilteringByClassNameNegative() {
        val filter = fromTestTargets(listOf("notClass whatever.Foo"))

        assertThat(filter.shouldRun(FOO_CLASSNAME)).isFalse()
        assertThat(filter.shouldRun(BAR_CLASSNAME)).isTrue()
    }

    @Test
    fun `empty targets should not filter @Ignore annotated tests`() {
        val filter = fromTestTargets(listOf())

        assertThat(filter.shouldRun(WITH_IGNORE_ANNOTATION)).isTrue()
        assertThat(filter.shouldRun(WITHOUT_IGNORE_ANNOTATION)).isTrue()
    }

    @Test
    fun testFilteringByAnnotation() {
        val filter = fromTestTargets(listOf("annotation Foo,Bar"))

        assertThat(filter.shouldRun(WITH_FOO_ANNOTATION)).isTrue()
        assertThat(filter.shouldRun(WITH_BAR_ANNOTATION)).isTrue()
        assertThat(filter.shouldRun(WITHOUT_FOO_ANNOTATION)).isFalse()
    }

    @Test
    fun testFilteringByAnnotationWithSpaces() {
        val filter = fromTestTargets(listOf("annotation Foo, Bar"))

        assertThat(filter.shouldRun(WITH_FOO_ANNOTATION)).isTrue()
        assertThat(filter.shouldRun(WITH_BAR_ANNOTATION)).isTrue()
        assertThat(filter.shouldRun(WITHOUT_FOO_ANNOTATION)).isFalse()
    }

    @Test
    fun testFilteringBySizeLarge() {
        val filter = fromTestTargets(listOf("size large"))

        assertThat(filter.shouldRun(WITH_LARGE_ANNOTATION)).isTrue()
        assertThat(filter.shouldRun(WITHOUT_LARGE_ANNOTATION)).isFalse()
    }

    @Test
    fun testFilteringBySizeMedium() {
        val filter = fromTestTargets(listOf("size medium"))

        assertThat(filter.shouldRun(WITH_MEDIUM_ANNOTATION)).isTrue()
        assertThat(filter.shouldRun(WITHOUT_MEDIUM_ANNOTATION)).isFalse()
    }

    @Test
    fun testFilteringBySizeSmall() {
        val filter = fromTestTargets(listOf("size small"))

        assertThat(filter.shouldRun(WITH_SMALL_ANNOTATION)).isTrue()
        assertThat(filter.shouldRun(WITHOUT_SMALL_ANNOTATION)).isFalse()
    }

    @Test(expected = FlankConfigurationError::class)
    fun testFilteringBySizeInvalidWillThrowException() {
        fromTestTargets(listOf("size foo"))
    }

    @Test
    fun testFilteringBySizes() {
        val filter = fromTestTargets(listOf("size large,small"))

        assertThat(filter.shouldRun(WITH_LARGE_ANNOTATION)).isTrue()
        assertThat(filter.shouldRun(WITH_SMALL_ANNOTATION)).isTrue()
        assertThat(filter.shouldRun(WITHOUT_LARGE_ANNOTATION)).isFalse()
        assertThat(filter.shouldRun(WITHOUT_SMALL_ANNOTATION)).isFalse()
    }

    @Test
    fun testFilteringBySizesWithSpace() {
        val filter = fromTestTargets(listOf("size large, small"))

        assertThat(filter.shouldRun(WITH_LARGE_ANNOTATION)).isTrue()
        assertThat(filter.shouldRun(WITH_SMALL_ANNOTATION)).isTrue()
        assertThat(filter.shouldRun(WITHOUT_LARGE_ANNOTATION)).isFalse()
        assertThat(filter.shouldRun(WITHOUT_SMALL_ANNOTATION)).isFalse()
    }

    @Test(expected = IllegalArgumentException::class)
    fun testFilteringByNotSizesWillThrowException() {
        fromTestTargets(listOf("notSize large"))
    }

    @Test
    fun testFilteringByAnnotationNegative() {
        val filter = fromTestTargets(listOf("notAnnotation Foo"))

        assertThat(filter.shouldRun(WITH_FOO_ANNOTATION)).isFalse()
        assertThat(filter.shouldRun(WITHOUT_FOO_ANNOTATION)).isTrue()
    }

    @Test
    fun allOfProperlyChecksAllFilters() {
        val filter = fromTestTargets(listOf("package foo,bar", "annotation Foo"))

        assertThat(filter.shouldRun(FOO_PACKAGE)).isFalse()
        assertThat(filter.shouldRun(BAR_PACKAGE)).isFalse()
        assertThat(filter.shouldRun(WITH_FOO_ANNOTATION)).isFalse()
        assertThat(filter.shouldRun(WITH_FOO_ANNOTATION_AND_PACKAGE)).isTrue()
    }

    @Test
    fun testFilteringFromFileNegative() {
        val file = TestHelper.getPath(TEST_FILE)
        val filePath = file.toString()

        val filter = fromTestTargets(listOf("testFile $filePath"))

        assertThat(filter.shouldRun(FOO_PACKAGE)).isTrue()
        assertThat(filter.shouldRun(BAR_PACKAGE)).isTrue()
    }

    @Test
    fun testFilteringFromFile() {
        val file = TestHelper.getPath(TEST_FILE)
        val filePath = file.toString()

        val filter = fromTestTargets(listOf("notTestFile $filePath"))

        assertThat(filter.shouldRun(FOO_PACKAGE)).isFalse()
        assertThat(filter.shouldRun(BAR_PACKAGE)).isFalse()
    }

    @Test(expected = IllegalArgumentException::class)
    fun passingMalformedCommandWillThrowException() {
        fromTestTargets(listOf("class=com.my.package"))
    }

    @Test(expected = IllegalArgumentException::class)
    fun passingInvalidCommandWillThrowException() {
        fromTestTargets(listOf("invalidCommand com.my.package"))
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
            output.add("""$result ${test.testName} [${filter.describe}]""")
            result
        }.map { "class ${it.testName}" }.toList()

        val expected = listOf(
            "false anyPackage_1.anyClass_1#anyMethod_1 [allOf [not (withAnnotation (Foo)), anyOf [withClassName (anyPackage_2.anyClass_2#anyMethod_2), withClassName (anyPackage_3.anyClass_3#anyMethod_3)]]]",
            "false anyPackage_2.anyClass_2#anyMethod_2 [allOf [not (withAnnotation (Foo)), anyOf [withClassName (anyPackage_2.anyClass_2#anyMethod_2), withClassName (anyPackage_3.anyClass_3#anyMethod_3)]]]",
            "true anyPackage_3.anyClass_3#anyMethod_3 [allOf [not (withAnnotation (Foo)), anyOf [withClassName (anyPackage_2.anyClass_2#anyMethod_2), withClassName (anyPackage_3.anyClass_3#anyMethod_3)]]]",
            "false anyPackage_4.anyClass_4#anyMethod_4 [allOf [not (withAnnotation (Foo)), anyOf [withClassName (anyPackage_2.anyClass_2#anyMethod_2), withClassName (anyPackage_3.anyClass_3#anyMethod_3)]]]"
        )

        assertThat(output).isEqualTo(expected)
        assertThat(filtered).isEqualTo(listOf("class anyPackage_3.anyClass_3#anyMethod_3"))
    }

    @Test
    fun notAnnotationFiltersWithClass() {
        val filter = fromTestTargets(listOf("notAnnotation Foo", "class anyPackage_1.anyClass_1#anyMethod_1"))
        val filtered = testMethodSet.withFilter(filter)

        assertEquals(listOf("anyPackage_1.anyClass_1#anyMethod_1"), filtered)
    }

    @Test
    fun notAnnotationFilters() {
        val filter = fromTestTargets(listOf("notAnnotation Moo"))
        val filtered = testMethodSet.withFilter(filter, enrich = false)

        assertEquals(commonExpected, filtered)
    }

    @Test
    fun methodOverrideIgnored() {
        val filter = fromTestTargets(targets.map { it.methodView })
        val filtered = testMethodSet.withFilter(filter)

        assertEquals(commonExpected, filtered)
    }

    @Test
    fun multipleClassesResolveToMethods() {
        val filter = fromTestTargets(targets.map { it.classView })
        val filtered = testMethodSet.withFilter(filter)

        assertEquals(commonExpected, filtered)
    }

    @Test
    fun multiplePackagesResolveToMethods() {
        val filter = fromTestTargets(targets.map { it.packageView })
        val filtered = testMethodSet.withFilter(filter)

        assertEquals(commonExpected, filtered)
    }

    @Test
    fun `by default - should contain method annotated with @Ignore`() {
        val byPackageFilter = fromTestTargets(targets.map { it.packageView })
        val byClassFilter = fromTestTargets(targets.map { it.classView })
        val byNotAnnotationFilter = fromTestTargets(listOf("notAnnotation ThereIsNoSuchAnnotation"))

        val byPackage = testMethodSet.withFilter(byPackageFilter)
        val byClass = testMethodSet.withFilter(byClassFilter)
        val byNotAnnotation = testMethodSet.withFilter(byNotAnnotationFilter, enrich = false)

        assertEquals(commonExpected, byPackage)
        assertEquals(commonExpected, byClass)
        assertEquals(commonExpected, byNotAnnotation)
    }

    @Test
    fun `should filter tests annotated with @Ignore if user explicitly want to do so`() {
        val byNotAnnotationFilter = fromTestTargets(listOf("notAnnotation $IGNORE_ANNOTATION"))
        val byNotAnnotation = testMethodSet.withFilter(byNotAnnotationFilter, enrich = false)
        val expected = targets.filterNot { it.annotation == IGNORE_ANNOTATION }.map { it.fullView }

        assertEquals(byNotAnnotation, expected)
    }

    @Test
    fun testFilteringClassAndPackageNegative() {
        val filter = fromTestTargets(listOf("notPackage foo", "notClass whatever.Bar"))

        assertThat(filter.shouldRun(FOO_PACKAGE)).isFalse()
        assertThat(filter.shouldRun(BAR_CLASSNAME)).isFalse()
        assertThat(filter.shouldRun(BAR_PACKAGE)).isTrue()
    }

    @Test
    fun testFilteringClassAndPackageNegativeFromFile() {
        val file = TestHelper.getPath(TEST_FILE_2) // contents: foo whatever.Bar
        val filePath = file.toString()

        val filter = fromTestTargets(listOf("notTestFile $filePath"))

        assertThat(filter.shouldRun(FOO_PACKAGE)).isFalse()
        assertThat(filter.shouldRun(BAR_CLASSNAME)).isFalse()
        assertThat(filter.shouldRun(BAR_PACKAGE)).isTrue()
    }

    @Test
    fun `inclusion filter should override exclusion filter`() {
        val filter = fromTestTargets(listOf("notPackage foo", "class whatever.Bar"))

        assertThat(filter.shouldRun(FOO_PACKAGE)).isFalse()
        assertThat(filter.shouldRun(BAR_PACKAGE)).isFalse()
        assertThat(filter.shouldRun(BAR_CLASSNAME)).isTrue()
    }
}

private fun getDefaultTestMethod(testName: String, annotation: String) =
    TestMethod(testName, listOf(TestAnnotation(annotation, emptyMap(), false)))

private fun List<TestMethod>.add(method: TestMethod) = listOf(*this.toTypedArray() + method)

private fun List<TestMethod>.withFilter(filter: TestFilter, enrich: Boolean = true) =
    if (enrich)
        add(getDefaultTestMethod("should.be#filtered", "AnyAnnotation"))
            .filter(filter.shouldRun)
            .map { it.testName }
    else
        filter(filter.shouldRun).map { it.testName }

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
