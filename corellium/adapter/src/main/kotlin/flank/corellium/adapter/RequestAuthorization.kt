package flank.corellium.adapter

import flank.corellium.api.Authorization
import flank.corellium.client.core.connectCorellium
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

val requestAuthorization = Authorization.Request { credentials ->
    GlobalScope.launch {
        corelliumRef = connectCorellium(
            api = credentials.host,
            username = credentials.username,
            password = credentials.password
        )
    }
}
