package ftl.util

import ftl.run.exception.FlankGeneralError
import java.io.Reader
import java.nio.file.Files
import java.nio.file.NoSuchFileException
import java.nio.file.Path

fun loadFile(filePath: Path): Reader = try {
    Files.newBufferedReader(filePath)
} catch (fileNotFound: NoSuchFileException) {
    throw FlankGeneralError("File not found: ${fileNotFound.message}")
}
