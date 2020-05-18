package ftl.args.yml.loader

import ftl.util.FlankNoSuchFileException
import java.io.Reader
import java.nio.file.Files
import java.nio.file.NoSuchFileException
import java.nio.file.Path

fun loadYamlFile(filePath: Path): Reader = try {
    Files.newBufferedReader(filePath)
} catch (fileNotFound: NoSuchFileException) {
    throw FlankNoSuchFileException("YAML file not found: ${fileNotFound.message}")
}
