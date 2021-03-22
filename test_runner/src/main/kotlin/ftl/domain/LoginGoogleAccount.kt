package ftl.domain

import ftl.gc.UserAuth

interface LoginGoogleAccount

operator fun LoginGoogleAccount.invoke() {
    UserAuth().request()
}
