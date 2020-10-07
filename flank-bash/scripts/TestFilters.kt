private const val PACKAGE = "com.example.test_app"
private const val PACKAGE_FOO = "com.example.test_app.foo"
private const val PACKAGE_BAR = "com.example.test_app.bar"

private const val ANNOTATION = "$PACKAGE.Annotation"

private const val CLASS = "$PACKAGE.InstrumentedTest"
private const val CLASS_FOO = "$PACKAGE_FOO.FooInstrumentedTest"
private const val CLASS_BAR = "$PACKAGE_BAR.BarInstrumentedTest"

private const val METHOD_1 = "$CLASS#test1"
private const val METHOD_FOO = "$CLASS_FOO#testFoo"
private const val METHOD_BAR = "$CLASS_BAR#testBar"

private const val RUNNER = "com.example.test_app.test/androidx.test.runner.AndroidJUnitRunner"

val runAllTestsShellCommand: String
    get() = "adb shell am instrument -r -w \$@ $RUNNER"

val filterPackageBarClassFooMethod1Command: String
    get() = "$runAllTestsShellCommand -e package $PACKAGE_BAR -e class $CLASS_FOO -e class $METHOD_1"

val filterAnnotationMethodFooCommand: String
    get() = "$runAllTestsShellCommand -e annotation $ANNOTATION -e class $METHOD_FOO"

val filterNotPackageFooNotClassBarCommand: String
    get() = "$runAllTestsShellCommand -e notPackage $PACKAGE_FOO -e notClass $CLASS_BAR"
