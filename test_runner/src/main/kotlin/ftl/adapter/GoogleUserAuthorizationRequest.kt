package ftl.adapter

import ftl.adapter.google.UserAuth
import ftl.data.UserAuthorization

object GoogleUserAuthorizationRequest :
    UserAuthorization.Request,
    () -> Unit by {
        UserAuth().request()
    }
