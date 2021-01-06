package flank.common

fun isCI() = System.getenv("CI") != null
