package ftl.filter

import com.linkedin.dex.parser.TestAnnotation
import com.linkedin.dex.parser.TestMethod
import ftl.test.util.FlankTestRunner
import ftl.test.util.TestHelper
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
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
        val filter = TestFilters.fromTestTargets(listOf("package foo"))

        assertTrue(filter(FOO_PACKAGE))
        assertFalse(filter(BAR_PACKAGE))
    }

    @Test
    fun testFilteringByPackageNegative() {
        val filter = TestFilters.fromTestTargets(listOf("notPackage foo"))

        assertFalse(filter(FOO_PACKAGE))
        assertTrue(filter(BAR_PACKAGE))
    }

    @Test
    fun testFilteringByClassName() {
        val filter = TestFilters.fromTestTargets(listOf("class whatever.Foo"))

        assertTrue(filter(FOO_CLASSNAME))
        assertFalse(filter(BAR_CLASSNAME))
    }

    @Test
    fun testFilteringByClassNameNegative() {
        val filter = TestFilters.fromTestTargets(listOf("notClass whatever.Foo"))

        assertFalse(filter(FOO_CLASSNAME))
        assertTrue(filter(BAR_CLASSNAME))
    }

    @Test
    fun emptyTargetsShouldFilterTestsWithTheIgnoreAnnotation() {
        val filter = TestFilters.fromTestTargets(listOf())

        assertFalse(filter(WITH_IGNORE_ANNOTATION))
        assertTrue(filter(WITHOUT_IGNORE_ANNOTATION))
    }

    @Test
    fun testFilteringByAnnotation() {
        val filter = TestFilters.fromTestTargets(listOf("annotation Foo,Bar"))

        assertTrue(filter(WITH_FOO_ANNOTATION))
        assertTrue(filter(WITH_BAR_ANNOTATION))
        assertFalse(filter(WITHOUT_FOO_ANNOTATION))
    }

    @Test
    fun testFilteringByAnnotationWithSpaces() {
        val filter = TestFilters.fromTestTargets(listOf("annotation Foo, Bar"))

        assertTrue(filter(WITH_FOO_ANNOTATION))
        assertTrue(filter(WITH_BAR_ANNOTATION))
        assertFalse(filter(WITHOUT_FOO_ANNOTATION))
    }

    @Test
    fun testFilteringBySizeLarge() {
        val filter = TestFilters.fromTestTargets(listOf("size large"))

        assertTrue(filter(WITH_LARGE_ANNOTATION))
        assertFalse(filter(WITHOUT_LARGE_ANNOTATION))
    }

    @Test
    fun testFilteringBySizeMedium() {
        val filter = TestFilters.fromTestTargets(listOf("size medium"))

        assertTrue(filter(WITH_MEDIUM_ANNOTATION))
        assertFalse(filter(WITHOUT_MEDIUM_ANNOTATION))
    }

    @Test
    fun testFilteringBySizeSmall() {
        val filter = TestFilters.fromTestTargets(listOf("size small"))

        assertTrue(filter(WITH_SMALL_ANNOTATION))
        assertFalse(filter(WITHOUT_SMALL_ANNOTATION))
    }

    @Test(expected = IllegalArgumentException::class)
    fun testFilteringBySizeInvalidWillThrowException() {
        TestFilters.fromTestTargets(listOf("size foo"))
    }

    @Test
    fun testFilteringBySizes() {
        val filter = TestFilters.fromTestTargets(listOf("size large,small"))

        assertTrue(filter(WITH_LARGE_ANNOTATION))
        assertTrue(filter(WITH_SMALL_ANNOTATION))
        assertFalse(filter(WITHOUT_LARGE_ANNOTATION))
        assertFalse(filter(WITHOUT_SMALL_ANNOTATION))
    }

    @Test
    fun testFilteringBySizesWithSpace() {
        val filter = TestFilters.fromTestTargets(listOf("size large, small"))

        assertTrue(filter(WITH_LARGE_ANNOTATION))
        assertTrue(filter(WITH_SMALL_ANNOTATION))
        assertFalse(filter(WITHOUT_LARGE_ANNOTATION))
        assertFalse(filter(WITHOUT_SMALL_ANNOTATION))
    }

    @Test(expected = IllegalArgumentException::class)
    fun testFilteringByNotSizesWillThrowException() {
        TestFilters.fromTestTargets(listOf("notSize large"))
    }

    @Test
    fun testFilteringByAnnotationNegative() {
        val filter = TestFilters.fromTestTargets(listOf("notAnnotation Foo"))

        assertFalse(filter(WITH_FOO_ANNOTATION))
        assertTrue(filter(WITHOUT_FOO_ANNOTATION))
    }

    @Test
    fun allOfProperlyChecksAllFilters() {
        val filter = TestFilters.fromTestTargets(listOf("package foo,bar", "annotation Foo"))

        assertFalse(filter(FOO_PACKAGE))
        assertFalse(filter(BAR_PACKAGE))
        assertFalse(filter(WITH_FOO_ANNOTATION))
        assertTrue(filter(WITH_FOO_ANNOTATION_AND_PACKAGE))
    }

    @Test
    fun testFilteringFromFileNegative() {
        val file = TestHelper.getPath(TEST_FILE)
        val filePath = file.toString()

        val filter = TestFilters.fromTestTargets(listOf("testFile $filePath"))

        assertTrue(filter(FOO_PACKAGE))
        assertTrue(filter(BAR_PACKAGE))
    }

    @Test
    fun testFilteringFromFile() {
        val file = TestHelper.getPath(TEST_FILE)
        val filePath = file.toString()

        val filter = TestFilters.fromTestTargets(listOf("notTestFile $filePath"))

        assertFalse(filter(FOO_PACKAGE))
        assertFalse(filter(BAR_PACKAGE))
    }

    @Test(expected = IllegalArgumentException::class)
    fun passingMalformedCommandWillThrowException() {
        TestFilters.fromTestTargets(listOf("class=com.my.package"))
    }

    @Test(expected = IllegalArgumentException::class)
    fun passingInvalidCommandWillThrowException() {
        TestFilters.fromTestTargets(listOf("invalidCommand com.my.package"))
    }
}
