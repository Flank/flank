package ftl.shard

data class Chunk(val testMethods: List<TestMethod>) {
    val testsList = testMethods.map { it.name }
    val size get() = testMethods.size
}

val List<Chunk>.testCases
    get() = map { it.testsList }
