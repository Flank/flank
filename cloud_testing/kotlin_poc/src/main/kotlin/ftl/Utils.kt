package ftl

import java.time.Instant
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.*


object Utils {

    fun sleep(milliseconds: Int) {
        try {
            Thread.sleep(milliseconds.toLong())
        } catch (e: Exception) {
        }

    }

    fun fatalError(e: Exception) {
        e.printStackTrace()
        System.exit(-1)
    }

    fun fatalError(e: String) {
        System.err.println(e)
        System.exit(-1)
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
                        .format(instant))

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

}
