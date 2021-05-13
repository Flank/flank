package ftl.adapter

import ftl.api.UserAuthorization
import ftl.client.google.UserAuth

object GoogleUserAuthorizationRequest :
    UserAuthorization.Request,
    () -> Unit by {
        UserAuth().request()
    }
