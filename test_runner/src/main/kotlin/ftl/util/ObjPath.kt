package ftl.util

import java.nio.file.Paths

data class ObjPath(
    val fileName: String,
    val objName: String,
    val shardName: String,
    val deviceName: String,
    val filePathName: String
) {
    companion object {
        fun parse(path: String): ObjPath {
            val parsed = Paths.get(path)

            val fileName = parsed.fileName.toString()
            val objName = parsed.getName(0).toString()
            val shardName = parsed.getName(1).toString()
            val deviceName = parsed.getName(2).toString()
            val filePathName = if (parsed.nameCount > 4) {
                parsed.subpath(3, parsed.nameCount - 1).toString()
            } else { "" }

            return ObjPath(
                fileName = fileName,
                objName = objName,
                shardName = shardName,
                deviceName = deviceName,
                filePathName = filePathName
            )
        }
    }
}
