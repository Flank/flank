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
        return NEW_LINE + listOfList.filterNotNull()
            .joinToString(System.lineSeparator()) { "         $it" }
    }

    fun apksToString(devices: List<AppTestPair>): String {
        if (devices.isNullOrEmpty()) return ""
        return NEW_LINE + devices.joinToString(System.lineSeparator()) { (app, test) -> "        - app: $app\n          test: $test" }
    }
}
