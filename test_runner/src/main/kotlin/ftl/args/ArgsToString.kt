package ftl.args

import ftl.args.yml.AppTestPair

private const val NEW_LINE = '\n'

object ArgsToString {

    fun mapToString(map: Map<String, String>?): String {
        if (map.isNullOrEmpty()) return ""
        return NEW_LINE + map.map { (key, value) -> "        $key: $value" }
            .joinToString(System.lineSeparator())
    }

    fun listToString(list: List<String?>?): String {
        if (list.isNullOrEmpty()) return ""
        return NEW_LINE + list.filterNotNull()
            .joinToString(System.lineSeparator()) { dir -> "        - $dir" }
    }

    fun objectsToString(objects: List<Any?>?): String {
        if (objects.isNullOrEmpty()) return ""
        return NEW_LINE + objects.filterNotNull()
            .joinToString(System.lineSeparator()) { "$it" }
    }

    fun listOfListToString(listOfList: List<List<String?>>?): String {
        if (listOfList.isNullOrEmpty()) return ""
        return NEW_LINE + listOfList.map { list -> list.joinToString(",") { " $it" } }
            .joinToString(System.lineSeparator()) { "        - $it" }
    }

    fun apksToString(devices: List<AppTestPair>): String {
        if (devices.isNullOrEmpty()) return ""

        return NEW_LINE + devices.joinToString(System.lineSeparator()) {

            val environmentVars = if (it.environmentVariables.isNotEmpty()) {
                "\n          environment-variables:\n" +
                    "            ${it.environmentVariables.toList().joinToString("\n            ") { pair -> "${pair.first}: ${pair.second}" }}"
            } else ""

            val clientDetails = if (it.clientDetails.isNotEmpty()) {
                "\n          client-details:\n" +
                    "            ${it.clientDetails.toList().joinToString("\n            ") { pair -> "${pair.first}: ${pair.second}" }}"
            } else ""

            "        - app: ${it.app}\n          test: ${it.test}$clientDetails$environmentVars"
        }
    }
}
