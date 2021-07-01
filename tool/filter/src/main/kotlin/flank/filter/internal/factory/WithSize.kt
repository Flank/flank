package flank.filter.internal.factory

import flank.filter.internal.Test

internal fun withSize(args: List<String>): Test.Filter {
    val filter = args.map(sizeAnnotations::getValue)
    return Test.Filter(
        describe = "withSize (${filter.joinToString(", ")})",
        shouldRun = { (_, annotations) ->
            // Ensure that all annotation with a name matching this size are detected, like
            // https://developer.android.com/reference/android/support/test/filters/LargeTest or
            // https://developer.android.com/reference/androidx/test/filters/LargeTest
            annotations.any { it.split(".").last() in filter }
        },
        isAnnotation = true
    )
}

private val sizeAnnotations: Map<String, String> =
    setOf(
        Test.Target.Size.LARGE,
        Test.Target.Size.MEDIUM,
        Test.Target.Size.SMALL,
    ).associateWith { size ->
        size.replaceFirstChar { char -> char.uppercaseChar() } + "Test"
    }.withDefault { size ->
        throw IllegalArgumentException("Unknown size $size")
    }
