package ftl.gc

import com.google.auth.oauth2.ClientId
import com.google.auth.oauth2.MemoryTokensStorage
import com.google.auth.oauth2.UserAuthorizer
import com.google.auth.oauth2.UserCredentials
import ftl.config.FtlConstants
import io.ktor.application.call
import io.ktor.response.respondText
import io.ktor.routing.get
import io.ktor.routing.routing
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.net.URI
import java.nio.file.Path
import java.nio.file.Paths
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking

class UserAuth {

    companion object {
        private val home = System.getProperty("user.home")!!
        private val dotFlank = Paths.get(home, ".flank/")
        val userToken: Path = Paths.get(dotFlank.toString(), "UserToken")

        fun exists() = userToken.toFile().exists()
        fun load(): UserCredentials {
            return ObjectInputStream(FileInputStream(userToken.toFile())).use {
                it.readObject() as UserCredentials
            }
        }
    }

    private var waitingForUserAuth = true

    private val server = embeddedServer(Netty, 8085) {
        routing {
            // 'code' and 'scope' are passed back into the callback as parameters
            get("/oauth2callback") {
                authCode = call.parameters["code"] ?: ""
                call.respondText { "User authorized. Close the browser window." }

                waitingForUserAuth = false
            }
        }
    }

    var authCode = ""

    // https://github.com/bootstraponline/gcloud_cli/blob/40521a6e297830b9f652a9ab4d8002e309b4353a/google-cloud-sdk/platform/gsutil/gslib/utils/system_util.py#L177
    private val clientId = ClientId.newBuilder()
        .setClientId("32555940559.apps.googleusercontent.com")
        .setClientSecret("ZmssLNjJy2998hD4CTg2ejr2")
        .build()!!

    // https://github.com/bootstraponline/gcloud_cli/blob/e4b5e01610abad2e31d8a6edb20b17b2f84c5395/google-cloud-sdk/lib/googlecloudsdk/core/config.py#L167
    private val scopes = listOf("https://www.googleapis.com/auth/cloud-platform")

    private val tokenStore = MemoryTokensStorage()
    private val authorizer = UserAuthorizer.newBuilder()
        .setClientId(clientId)
        .setScopes(scopes)
        .setTokenStore(tokenStore)
        .build()!!
    private val userId = "flank"
    val uri: URI = URI.create("http://localhost:8085")

    private fun printAuthorizationUrl() {
        val url = authorizer.getAuthorizationUrl(userId, null, uri)
        println("Visit the following URL in your browser:")
        println(url)
    }

    fun request() {
        if (FtlConstants.useMock) return
        printAuthorizationUrl()

        server.start(wait = false)

        while (waitingForUserAuth) {
            runBlocking { delay(1000) }
        }

        // trade OAuth2 authorization code for tokens.
        //
        // https://developers.google.com/gdata/docs/auth/oauth#NoLibrary
        authorizer.getAndStoreCredentialsFromCode(userId, authCode, uri)

        server.stop(0, 0)

        val userCredential = authorizer.getCredentials(userId)

        dotFlank.toFile().mkdirs()
        ObjectOutputStream(FileOutputStream(userToken.toFile())).use {
            it.writeObject(userCredential)
        }

        println()
        println("User token saved to $userToken")
    }
}
