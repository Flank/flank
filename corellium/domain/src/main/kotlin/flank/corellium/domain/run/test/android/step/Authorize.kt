package flank.corellium.domain.run.test.android.step

import flank.corellium.domain.RunTestCorelliumAndroid

/**
 * The Initial step required to perform further remote calls to Corellium API.
 */
internal fun RunTestCorelliumAndroid.Context.authorize() = RunTestCorelliumAndroid.step {
    println("* Authorizing")
    api.authorize(args.credentials).join()
    this
}
