package com.github.flank.wrapper.internal

import flank.common.config.isTest
import flank.tool.analytics.mixpanel.Mixpanel
import io.sentry.Sentry

private const val SESSION_ID = "session.id"
private const val OS_NAME = "os.name"
private const val FLANK_WRAPPER_VERSION = "flank_wrapper.version"
private const val DSN = "https://2df245c4e26d44e1bff106a12637d5e4@o475862.ingest.sentry.io/5855220"

fun setupCrashReporter() {
    if (isTest().not()) {
        Sentry.init { options ->
            options.dsn = DSN
            options.tracesSampleRate = 1.0
        }

        logTags(
            SESSION_ID to Mixpanel.sessionId,
            OS_NAME to osName,
            FLANK_WRAPPER_VERSION to flankWrapperVersion,
        )
    }
}

private fun logTags(vararg tags: Pair<String, String>) {
    tags.forEach { (key, value) -> Sentry.setTag(key, value) }
}

private val osName: String
    get() = System.getProperty("os.name")

private val flankWrapperVersion by lazy {
    (object {}.javaClass.getResource("/version.txt")?.readText() ?: "UNKNOWN")
}

internal fun Throwable.report() {
    Sentry.captureException(this)
}
