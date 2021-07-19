package flank.corellium.domain.run.test.android.step

import flank.corellium.domain.RunTestCorelliumAndroid.Authorize
import flank.corellium.domain.RunTestCorelliumAndroid.context
import flank.exection.parallel.using

/**
 * Authorizing access to corellium backend, required to perform further remote calls to Corellium API.
 */
internal val authorize = Authorize using context {
    api.authorize(args.credentials).join()
}
