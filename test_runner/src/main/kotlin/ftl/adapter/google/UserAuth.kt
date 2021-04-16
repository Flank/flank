package ftl.adapter.google

import com.google.auth.oauth2.ClientId
import com.google.auth.oauth2.MemoryTokensStorage
import com.google.auth.oauth2.UserAuthorizer
import com.google.auth.oauth2.UserCredentials
import flank.common.logLn
import flank.common.userHome
import ftl.config.FtlConstants
import ftl.run.exception.FlankGeneralError
import io.ktor.application.call
import io.ktor.response.respondText
import io.ktor.routing.get
import io.ktor.routing.routing
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.net.URI
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

class UserAuth {

    companion object {
        private val dotFlank = Paths.get(userHome, ".flank")
        val userToken: Path = if (FtlConstants.useMock) File.createTempFile("test_", ".Token").toPath()
        else Paths.get(dotFlank.toString(), "UserToken")

        fun exists() = Files.exists(userToken)
        fun load(): UserCredentials = readCredentialsOrThrow()

        private fun readCredentialsOrThrow(): UserCredentials = runCatching {
            FileInputStream(userToken.toFile()).use {
                ObjectInputStream(it).readObject() as UserCredentials
            }
        }.getOrElse {
            throwAuthenticationError()
        }

        fun throwAuthenticationError(): Nothing {
            Files.deleteIfExists(userToken)
            throw FlankGeneralError(
                "Could not load user authentication, please\n" +
                    " - login again using command: flank auth login\n" +
                    " - or try again to use The Application Default Credentials variable to login"
            )
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
        logLn("Visit the following URL in your browser:")
        logLn(url)
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
        ObjectOutputStream(FileOutputStream(userToken.toFile())).writeObject(userCredential)

        logLn()
        logLn("User token saved to $userToken")
    }
}
