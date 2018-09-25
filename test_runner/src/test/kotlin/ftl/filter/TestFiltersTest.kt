package ftl.filter

import com.google.common.truth.Truth.assertThat
import com.linkedin.dex.parser.TestAnnotation
import com.linkedin.dex.parser.TestMethod
import ftl.filter.TestFilters.fromTestTargets
import ftl.test.util.FlankTestRunner
import ftl.test.util.TestHelper
import org.junit.Test
import org.junit.runner.RunWith

val FOO_PACKAGE = TestMethod("foo.ClassName#testName", emptyList())
val BAR_PACKAGE = TestMethod("bar.ClassName#testName", emptyList())
val FOO_CLASSNAME = TestMethod("whatever.Foo#testName", emptyList())
val BAR_CLASSNAME = TestMethod("whatever.Bar#testName", emptyList())
val WITHOUT_IGNORE_ANNOTATION = TestMethod("whatever.Foo#testName", emptyList())
val WITH_IGNORE_ANNOTATION = TestMethod("whatever.Foo#testName", listOf(TestAnnotation("Ignore", emptyMap())))
val WITH_FOO_ANNOTATION = TestMethod("whatever.Foo#testName", listOf(TestAnnotation("Foo", emptyMap())))
val WITH_BAR_ANNOTATION = TestMethod("whatever.Foo#testName", listOf(TestAnnotation("Bar", emptyMap())))
val WITHOUT_FOO_ANNOTATION = TestMethod("whatever.Foo#testName", emptyList())
val WITH_FOO_ANNOTATION_AND_PACKAGE = TestMethod("foo.Bar#testName", listOf(TestAnnotation("Foo", emptyMap())))
val WITH_LARGE_ANNOTATION = TestMethod("whatever.Foo#testName", listOf(TestAnnotation("LargeTest", emptyMap())))
val WITH_MEDIUM_ANNOTATION = TestMethod("whatever.Foo#testName", listOf(TestAnnotation("MediumTest", emptyMap())))
val WITH_SMALL_ANNOTATION = TestMethod("whatever.Foo#testName", listOf(TestAnnotation("SmallTest", emptyMap())))
val WITHOUT_LARGE_ANNOTATION = TestMethod("whatever.Foo#testName", emptyList())
val WITHOUT_MEDIUM_ANNOTATION = TestMethod("whatever.Foo#testName", emptyList())
val WITHOUT_SMALL_ANNOTATION = TestMethod("whatever.Foo#testName", emptyList())
const val TEST_FILE = "src/test/kotlin/ftl/filter/fixtures/dummy-tests-file.txt"

@RunWith(FlankTestRunner::class)
class TestFiltersTest {
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
    fun emptyTargetsShouldFilterTestsWithTheIgnoreAnnotation() {
        val filter = fromTestTargets(listOf())

        assertThat(filter.shouldRun(WITH_IGNORE_ANNOTATION)).isFalse()
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

    @Test(expected = IllegalArgumentException::class)
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
        val filter = TestFilters.fromTestTargets(listOf("package foo,bar", "annotation Foo"))

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

    private fun getTestMethodSet(): List<TestMethod> {
        val m1 = TestMethod("a.b#c", listOf(TestAnnotation("Ignore", emptyMap())))
        val m2 = TestMethod("d.e#f", listOf(TestAnnotation("Foo", emptyMap())))
        val m3 = TestMethod("h.i#j", listOf(TestAnnotation("Bar", emptyMap())))
        return listOf(m1, m2, m3)
    }

    @Test
    fun classFilterOverridesNotAnnotation() {
        val testMethods = getTestMethodSet()
        val filter = fromTestTargets(listOf("notAnnotation Foo", "class d.e#f", "class h.i#j"))

        val output = mutableListOf<String>()
        val filtered = testMethods.asSequence().filter { test ->
            val result = filter.shouldRun(test)
            output.add("""$result ${test.testName} [${filter.describe}]""")
            result
        }.map { "class ${it.testName}" }.toList()

        // @Ignore a.b#c
        // @Foo d.e#f
        // @Bar h.i#j
        val expected = listOf(
            "false a.b#c [allOf [notIgnored, not withAnnotation Foo, anyOf [withClassName d.e#f, withClassName h.i#j]]]",
            "false d.e#f [allOf [notIgnored, not withAnnotation Foo, anyOf [withClassName d.e#f, withClassName h.i#j]]]",
            "true h.i#j [allOf [notIgnored, not withAnnotation Foo, anyOf [withClassName d.e#f, withClassName h.i#j]]]"
        )

        assertThat(output).isEqualTo(expected)
        assertThat(filtered).isEqualTo(listOf("class h.i#j"))
    }

    @Test
    fun notAnnotationFiltersWithClass() {
        val testMethods = getTestMethodSet()
        val filter = fromTestTargets(listOf("notAnnotation Foo", "class h.i#j"))

        val filtered = testMethods.asSequence().filter(filter.shouldRun).map { it.testName }.toList()
        assertThat(filtered).isEqualTo(listOf("h.i#j"))
    }

    @Test
    fun notAnnotationFilters() {
        val testMethods = getTestMethodSet()
        val filter = fromTestTargets(listOf("notAnnotation Moo"))

        val filtered = testMethods.asSequence().filter(filter.shouldRun).map { it.testName }.toList()
        assertThat(filtered).isEqualTo(listOf("d.e#f", "h.i#j"))
    }

    @Test
    fun methodOverrideIgnored() {
        val filter = fromTestTargets(listOf("class a.b#c", "class d.e#f", "class h.i#j"))

        val filtered = getTestMethodSet().asSequence().filter(filter.shouldRun).map { it.testName }.toList()
        assertThat(filtered).isEqualTo(listOf("d.e#f", "h.i#j"))
    }

    @Test
    fun multipleClassesResolveToMethods() {
        val filter = fromTestTargets(listOf("class a.b", "class d.e", "class h.i"))

        val filtered = getTestMethodSet().asSequence().filter(filter.shouldRun).map { it.testName }.toList()
        assertThat(filtered).isEqualTo(listOf("d.e#f", "h.i#j"))
    }

    @Test
    fun multiplePackagesResolveToMethods() {
        val filter = fromTestTargets(listOf("package a", "package d", "package h"))

        val filtered = getTestMethodSet().asSequence().filter(filter.shouldRun).map { it.testName }.toList()
        assertThat(filtered).isEqualTo(listOf("d.e#f", "h.i#j"))
    }
}
