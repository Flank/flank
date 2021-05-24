package ftl.reports.output

enum class OutputReportType {
    NONE,
    JSON;

    override fun toString() = super.toString().lowercase()

    companion object {
        fun fromName(name: String?) = if (name.equals(JSON.name, ignoreCase = true)) JSON else NONE
    }
}
