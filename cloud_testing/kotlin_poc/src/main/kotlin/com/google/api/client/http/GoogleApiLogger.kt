package com.google.api.client.http

import java.util.logging.Level
import java.util.logging.SimpleFormatter
import java.util.logging.StreamHandler

// Used to enable cURL logging of the Java client API requests.
//
// Compare with gcloud sdk traffic by using the --log-http flag
//
// gcloud alpha firebase test ios models list --log-http
//
object GoogleApiLogger {
    fun logAllToStdout() {
        val logger = HttpTransport.LOGGER
        logger.level = Level.ALL

        val handler = StreamHandler(System.out, SimpleFormatter())
        handler.level = Level.ALL
        logger.addHandler(handler)
    }

}
