package flank.corellium.adapter

import flank.corellium.api.Authorization
import flank.corellium.client.core.connectCorellium

object RequestAuthorization : Authorization.Request {
    override suspend fun Authorization.Credentials.invoke() {
        corelliumRef = connectCorellium(
            api = host,
            username = username,
            password = password
        )
    }
}
