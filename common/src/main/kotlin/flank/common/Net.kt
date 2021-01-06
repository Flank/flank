package flank.common

import java.net.InetAddress
import java.net.InetSocketAddress
import java.net.Socket

internal fun isOnline(): Boolean {
    val host = "1.1.1.1"

    try {
        if (InetAddress.getByName(host).isReachable(1000)) return true
    } catch (e: Exception) {
        println("Cannot ping $host: $e\n")
    }

    try {
        Socket().use { it.connect(InetSocketAddress(host, 53), 1000) }
        return true
    } catch (e: Exception) {
        println("Cannot connect to $host: $e\n")
    }

    return false
}
