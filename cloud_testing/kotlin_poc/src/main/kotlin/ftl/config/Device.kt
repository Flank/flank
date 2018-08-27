package ftl.config

data class Device(
        val model: String,
        val version: String,
        val locale: String = "en",
        val orientation: String = "portrait")
