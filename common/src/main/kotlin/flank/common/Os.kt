package flank.common

private val osName = System.getProperty("os.name")?.lowercase() ?: ""

val isMacOS: Boolean by lazy {
    val isMacOS = osName.indexOf("mac") >= 0
    logLn("isMacOS = $isMacOS ($osName)")
    isMacOS
}

val isWindows: Boolean by lazy {
    osName.indexOf("win") >= 0
}

val isLinux: Boolean
    get() = !isWindows && !isMacOS
