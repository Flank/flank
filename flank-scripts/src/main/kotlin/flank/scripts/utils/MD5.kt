package flank.scripts.utils

import java.security.MessageDigest
import javax.xml.bind.DatatypeConverter

fun ByteArray.md5(): String {
    val md5 = MessageDigest.getInstance("MD5")
    md5.update(this)
    return DatatypeConverter.printHexBinary(md5.digest())
}
