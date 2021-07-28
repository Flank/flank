package ftl.api

import ftl.adapter.GoogleUserAuthorizationRequest
import kotlinx.coroutines.flow.Flow
import java.net.URL

val requestUserAuthorization: UserAuthorization.Request get() = GoogleUserAuthorizationRequest

object UserAuthorization {
    interface Request : () -> Flow<LoginState>
}

sealed interface LoginState {
    data class LoginStarted(val url: URL) : LoginState
    data class LoginFinished(val tokenLocation: String) : LoginState
}
