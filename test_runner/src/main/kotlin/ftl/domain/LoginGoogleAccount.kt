package ftl.domain

import ftl.api.requestUserAuthorization

interface LoginGoogleAccount

operator fun LoginGoogleAccount.invoke() {
    requestUserAuthorization()
}
