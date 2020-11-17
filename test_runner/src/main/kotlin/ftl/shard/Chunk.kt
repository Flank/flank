package ftl.shard

data class Chunk(
    val testMethods: List<TestMethod>
) {
    val testMethodNames = testMethods.map { it.name }
    val size get() = testMethods.size
}

val List<Chunk>.testCases: List<List<String>>
    get() = map { it.testMethodNames }
