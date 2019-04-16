package ftl.args

import ftl.config.Device

object ArgsToString {

    fun mapToString(map: Map<String, String>?): String {
        if (map.isNullOrEmpty()) return ""
        return map.map { (key, value) -> "        $key: $value" }
            .joinToString("\n")
    }

    fun listToString(list: List<String?>?): String {
        if (list.isNullOrEmpty()) return ""
        return list.filterNotNull()
            .joinToString("\n") { dir -> "        - $dir" }
    }

    fun devicesToString(devices: List<Device?>?): String {
        if (devices.isNullOrEmpty()) return ""
        return devices.filterNotNull()
            .joinToString("\n") { "$it" }
    }
}
