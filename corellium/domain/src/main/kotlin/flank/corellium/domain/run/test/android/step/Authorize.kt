package flank.corellium.domain.run.test.android.step

import flank.corellium.domain.RunTestCorelliumAndroid
import flank.corellium.domain.RunTestCorelliumAndroid.Authorize
import flank.corellium.domain.step

/**
 * The Initial step required to perform further remote calls to Corellium API.
 */
internal fun RunTestCorelliumAndroid.Context.authorize() = step(Authorize) {
    api.authorize(args.credentials).join()
    this
}
