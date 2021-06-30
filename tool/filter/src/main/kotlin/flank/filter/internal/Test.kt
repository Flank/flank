package flank.filter.internal

import flank.filter.ShouldRun

internal object Test {

    /**
     * TestFilter similar to https://junit.org/junit4/javadoc/4.12/org/junit/runner/manipulation/Filter.html
     *
     * Annotations are tracked as all annotation filters must match on a test.
     *
     * @property describe - description of the filter
     * @property shouldRun - lambda that returns if a TestMethod should be included in the test run
     * **/
    data class Filter(
        val describe: String,
        val shouldRun: ShouldRun,
        val isAnnotation: Boolean = false
    )

    /**
     * Supports arguments defined in androidx.test.internal.runner.RunnerArgs
     *
     * Multiple annotation arguments will result in the intersection.
     * https://developer.android.com/reference/android/support/test/runner/AndroidJUnitRunner
     * https://cloud.google.com/sdk/gcloud/reference/firebase/test/android/run
     */
    internal object Target {

        object Type {
            const val ARGUMENT_TEST_CLASS = "class"
            const val ARGUMENT_NOT_TEST_CLASS = "notClass"

            const val ARGUMENT_TEST_SIZE = "size"

            const val ARGUMENT_ANNOTATION = "annotation"
            const val ARGUMENT_NOT_ANNOTATION = "notAnnotation"

            const val ARGUMENT_TEST_PACKAGE = "package"
            const val ARGUMENT_NOT_TEST_PACKAGE = "notPackage"

            const val ARGUMENT_TEST_FILE = "testFile"
            const val ARGUMENT_NOT_TEST_FILE = "notTestFile"
        }

        object Size {
            const val LARGE = "large"
            const val MEDIUM = "medium"
            const val SMALL = "small"
        }
    }
}
