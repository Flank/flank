package ftl.domain

import ftl.api.requestUserAuthorization
import ftl.presentation.Output
import kotlinx.coroutines.flow.collect

interface LoginGoogleAccount : Output

suspend operator fun LoginGoogleAccount.invoke() {
    requestUserAuthorization().collect { it.out() }
}
