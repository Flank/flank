package ftl.domain

import ftl.data.requestUserAuthorization

interface LoginGoogleAccount

operator fun LoginGoogleAccount.invoke() {
    requestUserAuthorization()
}
