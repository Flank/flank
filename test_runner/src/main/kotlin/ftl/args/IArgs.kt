package ftl.args

interface IArgs {
    val async: Boolean
    val resultsBucket: String
    val projectId: String
    val repeatTests: Int
    val testShardChunks: List<List<String>>
}
