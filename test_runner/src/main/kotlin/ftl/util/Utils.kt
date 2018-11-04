package ftl.util

import ftl.config.FtlConstants
import ftl.json.MatrixMap
import java.io.InputStream
import java.io.StringWriter
import java.nio.file.Files
import java.nio.file.Paths
import java.time.Duration.ofSeconds
import java.time.Instant
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.Random
import kotlin.system.exitProcess

object Utils {

    fun String.trimStartLine(): String {
        return this.split("\n").drop(1).joinToString("\n")
    }

    fun StringWriter.println(msg: String = "") {
        this.append(msg + "\n")
    }

    fun String.write(data: String) {
        Files.write(Paths.get(this), data.toByteArray())
    }

    fun join(first: String, vararg more: String): String {
        return Paths.get(first, *more).toString()
    }

    fun sleep(seconds: Long) {
        try {
            Thread.sleep(ofSeconds(seconds).toMillis())
        } catch (e: Exception) {
        }
    }

    // marked as inline to make JaCoCo happy
    @Suppress("NOTHING_TO_INLINE")
    inline fun fatalError(e: Exception) {
        throw RuntimeException(e)
    }

    @Suppress("NOTHING_TO_INLINE")
    inline fun fatalError(e: String) {
        if (FtlConstants.useMock) {
            throw RuntimeException(e)
        }
        System.err.println(e)
        exitProcess(-1)
    }

    fun assertNotEmpty(str: String, e: String) {
        if (str.isEmpty()) {
            fatalError(e)
        }
    }

    // Match _GenerateUniqueGcsObjectName from api_lib/firebase/test/arg_validate.py
    //
    // Example: 2017-05-31_17:19:36.431540_hRJD
    //
    // https://cloud.google.com/storage/docs/naming
    fun uniqueObjectName(): String {
        val bucketName = StringBuilder()
        val instant = Instant.now()

        bucketName.append(
            DateTimeFormatter.ofPattern("yyyy-MM-dd_HH:mm:ss.")
                .withZone(ZoneOffset.UTC)
                .format(instant)
        )

        val nanoseconds = instant.nano.toString()

        if (nanoseconds.length >= 6) {
            bucketName.append(nanoseconds.substring(0, 6))
        } else {
            bucketName.append(nanoseconds.substring(0, nanoseconds.length - 1))
        }

        bucketName.append("_")

        val random = Random()
        // a-z: 97 - 122
        // A-Z: 65 - 90
        for (i in 0..3) {
            val ascii = random.nextInt(26)
            var letter = (ascii + 'a'.toInt()).toChar()

            if (ascii % 2 == 0) {
                letter -= 32 // upcase
            }

            bucketName.append(letter)
        }

        bucketName.append("/")

        return bucketName.toString()
    }

    private val classLoader = Thread.currentThread().contextClassLoader

    private fun getResource(name: String): InputStream {
        return classLoader.getResourceAsStream(name)
            ?: throw RuntimeException("Unable to find resource: $name")
    }

    fun readTextResource(name: String): String {
        return getResource(name).bufferedReader().use { it.readText() }
    }

    private val userHome = System.getProperty("user.home")

    fun copyBinaryResource(name: String) {
        val destinationPath = Paths.get(userHome, ".flank", name)
        val destinationFile = destinationPath.toFile()

        if (destinationFile.exists()) return
        destinationPath.parent.toFile().mkdirs()

        // "binaries/" folder prefix is required for Linux to find the resource.
        val bytes = getResource("binaries/$name").use { it.readBytes() }
        Files.write(destinationPath, bytes)
        destinationFile.setExecutable(true)
    }

    /**
     * exit code 0 - all tests passed
     * exit code 1 - at least test failed & no FTL errors
     * exit code 2 - at least one infrastructure failure or other FTL error
     */
    fun getExitCode(matrices: MatrixMap): Int {
        val savedMatrices = matrices.map.values
        var exitCode = 0
        savedMatrices.forEach { matrix ->
            when (matrix.state) {
                MatrixState.ERROR,
                MatrixState.UNSUPPORTED_ENVIRONMENT,
                MatrixState.INCOMPATIBLE_ENVIRONMENT,
                MatrixState.INCOMPATIBLE_ARCHITECTURE,
                MatrixState.CANCELLED,
                MatrixState.INVALID -> {
                    return 2
                }
                MatrixState.FINISHED -> {
                    if (matrix.outcome != Outcome.success) {
                        exitCode = 1
                        if (matrix.outcome != Outcome.failure) {
                            return 2
                        }
                    }
                }
            }
        }
        return exitCode
    }
}
