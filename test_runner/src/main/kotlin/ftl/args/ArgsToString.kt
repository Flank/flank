package ftl.args

import ftl.config.Device

object ArgsToString {

    fun mapToString(map: Map<String, String>): String {
        return map.map { (key, value) -> "        $key: $value" }
            .joinToString("\n")
    }

    fun listToString(list: List<String>): String {
        return list.joinToString("\n") { dir -> "        - $dir" }
    }

    fun devicesToString(devices: List<Device>): String {
        return devices.joinToString("\n") { "$it" }
    }
}
