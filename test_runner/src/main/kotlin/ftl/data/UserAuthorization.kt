package ftl.data

import ftl.adapter.GoogleUserAuthorizationRequest

val requestUserAuthorization: UserAuthorization.Request get() = GoogleUserAuthorizationRequest

object UserAuthorization {
    interface Request : () -> Unit
}
