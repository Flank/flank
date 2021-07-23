package flank.corellium.domain.test.android.task

import flank.corellium.domain.TestAndroid.Authorize
import flank.corellium.domain.TestAndroid.context
import flank.exection.parallel.using

/**
 * Authorizes access to corellium backend, required to perform further remote calls to Corellium API.
 */
internal val authorize = Authorize using context {
    api.authorize(args.credentials).join()
}
