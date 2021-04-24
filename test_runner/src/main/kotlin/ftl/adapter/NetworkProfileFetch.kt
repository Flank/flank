package ftl.adapter

import ftl.adapter.google.toApiModel
import ftl.api.NetworkProfile
import ftl.client.google.getGoogleNetworkConfiguration

object NetworkProfileFetch :
    NetworkProfile.Fetch,
    () -> List<NetworkProfile> by {
        getGoogleNetworkConfiguration().toApiModel()
    }
