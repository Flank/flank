package ftl.ios.xctest.common

import ftl.config.FtlConstants
import ftl.util.copyBinaryResource

internal val installBinaries by lazy {
    when {
        FtlConstants.isMacOS -> Unit
        FtlConstants.isWindows -> {
            // copyBinaryResource("nm") // Will be available after #1134"
            // copyBinaryResource("swift-demangle") // Will be available after #1134"
            copyBinaryResource("libatomic.so.1") // swift-demangle dependency
            copyBinaryResource("libatomic.so.1.2.0")
        }
        else -> {
            copyBinaryResource("nm")
            copyBinaryResource("swift-demangle")
            copyBinaryResource("libatomic.so.1") // swift-demangle dependency
            copyBinaryResource("libatomic.so.1.2.0")
        }
    }
}
