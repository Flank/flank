package utils

import org.gradle.nativeplatform.platform.internal.DefaultNativePlatform

val isWindows: Boolean
    get() = DefaultNativePlatform.getCurrentOperatingSystem().isWindows
