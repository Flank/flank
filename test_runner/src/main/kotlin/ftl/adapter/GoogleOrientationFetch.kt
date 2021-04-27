package ftl.adapter

import ftl.adapter.google.toApiModel
import ftl.api.Orientation
import ftl.api.Platform
import ftl.client.google.AndroidCatalog
import ftl.client.google.IosCatalog

object GoogleOrientationFetch :
    Orientation.Fetch,
    (String, Platform) -> List<Orientation> by { projectId, platform ->
        when (platform) {
            Platform.ANDROID -> AndroidCatalog.supportedOrientations(projectId).toApiModel()
            Platform.IOS -> IosCatalog.supportedOrientations(projectId).toApiModel()
        }
    }
