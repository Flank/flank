package flank.corellium.adapter

import flank.corellium.client.core.Corellium

internal val corellium: Corellium
    get() = requireNotNull(corelliumRef) {
        "Corellium is not initialized, try to call connectCorellium at first."
    }

// It's totally ok to keep corellium as singleton since we don't need handle more than one connection for single run.
internal var corelliumRef: Corellium? = null
