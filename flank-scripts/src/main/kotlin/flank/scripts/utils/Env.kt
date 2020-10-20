package flank.scripts.utils

fun getEnv(key: String): String =
    System.getenv(key) ?: throw RuntimeException("Environment variable '$key' not found!")

val isWindows: Boolean = getEnv("os.name").startsWith("Windows")
