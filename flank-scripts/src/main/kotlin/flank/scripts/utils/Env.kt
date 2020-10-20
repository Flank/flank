package flank.scripts.utils

fun getEnv(key: String): String =
    System.getenv(key) ?: throw RuntimeException("Environment variable '$key' not found!")

val isWindows: Boolean = System.getProperty("os.name").startsWith("Windows")
