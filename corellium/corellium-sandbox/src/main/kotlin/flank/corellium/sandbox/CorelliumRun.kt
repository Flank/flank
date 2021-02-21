package flank.corellium.sandbox

import flank.corellium.client.Corellium
import flank.corellium.sandbox.config.Config

suspend fun main() {
    val client = Corellium(
        api = Config.api,
        username = Config.username,
        password = Config.password
    )

    client.logIn()

    client.getProjectIdList()
}
