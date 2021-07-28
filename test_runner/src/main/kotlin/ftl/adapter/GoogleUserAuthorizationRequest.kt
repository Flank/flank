package ftl.adapter

import ftl.api.LoginState
import ftl.api.UserAuthorization
import ftl.client.google.UserAuth
import kotlinx.coroutines.flow.Flow

object GoogleUserAuthorizationRequest :
    UserAuthorization.Request,
    () -> Flow<LoginState> by { UserAuth().request() }
