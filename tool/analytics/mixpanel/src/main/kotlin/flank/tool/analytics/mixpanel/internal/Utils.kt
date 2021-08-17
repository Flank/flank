package flank.tool.analytics.mixpanel.internal

import kotlin.math.max

internal fun retryWithSilentFailure(times: Int = 1, block: () -> Unit) {
    (0..max(0, times)).onEach { runCatching { block() }.onSuccess { return } }
}
