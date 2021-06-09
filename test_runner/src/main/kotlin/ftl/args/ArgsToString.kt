package ftl.args

import ftl.args.yml.AppTestPair

private const val NEW_LINE = '\n'
private const val SPACESx8 = "        "
private const val SPACESx10 = "          "

object ArgsToString {

    fun mapToString(
        map: Map<String, String>?,
        transform: (Map.Entry<String, String>) -> String = { (key, value) -> "$SPACESx8$key: $value" }
    ): String {
        if (map.isNullOrEmpty()) return ""
        return NEW_LINE + map.map(transform)
            .joinToString(System.lineSeparator())
    }

    fun listToString(list: List<String?>?, transform: (String) -> String = { "$SPACESx8- $it" }): String {
        if (list.isNullOrEmpty()) return ""
        return NEW_LINE + list.filterNotNull()
            .joinToString(System.lineSeparator(), transform = transform)
    }

    fun objectsToString(objects: List<Any?>?, transform: (Any) -> String = { "$it" }): String {
        if (objects.isNullOrEmpty()) return ""
        return NEW_LINE + objects.filterNotNull()
            .joinToString(System.lineSeparator(), transform = transform)
    }

    fun listOfListToString(listOfList: List<List<String?>>?): String {
        if (listOfList.isNullOrEmpty()) return ""
        return NEW_LINE + listOfList.map { list -> list.joinToString(",") { " $it" } }
            .joinToString(System.lineSeparator()) { "$SPACESx8- $it" }
    }

    fun apksToString(devices: List<AppTestPair>): String {
        if (devices.isNullOrEmpty()) return ""
        return NEW_LINE + devices.joinToString(System.lineSeparator()) { pair ->
            buildString {
                if (pair.app == null) appendLine("$SPACESx8- test: ${pair.test}")
                else {
                    appendLine("$SPACESx8- app: ${pair.app}")
                    appendLine("$SPACESx8  test: ${pair.test}")
                }
                pair.maxTestShards?.let { appendLine("$SPACESx8  max-test-shards: $it") }
                pair.clientDetails?.let {
                    append("$SPACESx8  client-details:${mapToString(it) { (key, value) -> "$SPACESx10  $key: $value" }}")
                }
                pair.testTargets?.let { list ->
                    appendLine("$SPACESx8  test-targets:${listToString(list) { "$SPACESx10- $it" }}")
                }
                pair.devices?.let { list ->
                    appendLine("$SPACESx8  device:")
                    list
                        .map { "  $it" }
                        .map { it.replace("\n", "\n  ") }
                        .forEach { append(it) }
                }
            }.trimEnd()
        }
    }
}
