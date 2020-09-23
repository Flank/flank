package flank.scripts.dependencies.update.testfiles

import java.io.File

fun prepareFileForTest(path: String) {
    File(path).renameTo(File(path.replace(".test", "")))
}

fun makeFileNotPrepared(path: String) {
    File(path.replace(".test", "")).renameTo(File(path))
}
