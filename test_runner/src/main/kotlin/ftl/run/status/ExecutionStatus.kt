package ftl.run.status

import ftl.config.FtlConstants

data class ExecutionStatus(
    val state: String = "",
    val error: String? = null,
    val progress: List<String> = emptyList()
) {
    data class Change(
        val name: String,
        val previous: ExecutionStatus,
        val current: ExecutionStatus,
        val time: String
    )
    data class View(
        val time: String,
        val id: String,
        val status: String
    ) {
        override fun toString() = "${FtlConstants.indent}$time $id $status"
    }
}
