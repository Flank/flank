package flank.corellium.adapter

import flank.corellium.client.core.Corellium

// TODO consider to wrap state into dynamic structure instead of providing static access

internal val corellium: Corellium
    get() = State.corellium
        ?: error("Corellium not initialized, try to call connectCorellium at first.")

internal object State {
    var corellium: Corellium? = null
}
