package ftl.cli.hypershard.android

@TestAnnotation
class ExampleTestWithAnnotation {

    fun test1WithAnnotation() {
        println("Executing test1WithAnnotation")
    }

    fun test2WithAnnotation() {
        println("Executing test2WithAnnotation")
    }
}

annotation class TestAnnotation
