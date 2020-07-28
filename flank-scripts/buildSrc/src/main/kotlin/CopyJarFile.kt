import java.io.File

fun copyJarFile(fromPath: String, toPath: String) {
    File(fromPath).copyTo(File(toPath), overwrite = true)
    println("Jar file copied to $toPath")
}
