package ftl.ios.xctest

import ftl.config.FtlConstants
import ftl.util.copyBinaryResource

internal val installBinaries by lazy {
    if (!FtlConstants.isMacOS) {
        copyBinaryResource("nm")
        copyBinaryResource("swift-demangle")
        copyBinaryResource("libatomic.so.1") // swift-demangle dependency
        copyBinaryResource("libatomic.so.1.2.0")
    }
}
