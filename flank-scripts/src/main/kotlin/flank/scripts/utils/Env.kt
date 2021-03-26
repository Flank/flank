package flank.scripts.utils

fun getEnv(key: String): String =
    System.getenv(key) ?: throw RuntimeException("Environment variable '$key' not found!")

fun getEnvOrDefault(key: String, default: String): String =
    System.getenv(key) ?: default

val isWindows: Boolean = System.getProperty("os.name").startsWith("Windows")
