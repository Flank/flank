package flank.scripts.utils

fun getEnv(key: String, default: String? = null): String =
    System.getenv(key) ?: default ?: throw RuntimeException("Environment variable '$key' not found!")
val isWindows: Boolean = System.getProperty("os.name").startsWith("Windows")
