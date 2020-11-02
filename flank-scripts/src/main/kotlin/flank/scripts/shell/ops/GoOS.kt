package flank.scripts.shell.ops

enum class GoOS(
    val goName: String,
    val directory: String,
    val extension: String = ""
) {
    LINUX("linux", "bin/linux"),
    MAC("darwin", "bin/mac"),
    WINDOWS("windows", "bin/win", ".exe"),
}
