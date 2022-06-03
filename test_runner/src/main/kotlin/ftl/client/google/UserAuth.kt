package ftl.client.google

import com.google.auth.oauth2.ClientId
import com.google.auth.oauth2.MemoryTokensStorage
import com.google.auth.oauth2.UserAuthorizer
import com.google.auth.oauth2.UserCredentials
import flank.common.dotFlank
import ftl.api.LoginState
import ftl.config.FtlConstants
import ftl.run.exception.FlankGeneralError
import io.ktor.server.application.call
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flow
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
    private val uri: URI = URI.create("http://localhost:8085")

    fun request(): Flow<LoginState> {
        if (FtlConstants.useMock) return emptyFlow()

        return flow {
            emit(LoginState.LoginStarted(authorizer.getAuthorizationUrl(userId, null, uri)))

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

            emit(LoginState.LoginFinished(userToken.toString()))
        }
    }
}
