package ftl.adapter.google

import ftl.api.Orientation
import com.google.testing.model.Orientation as GoogleApiOrientation

internal fun List<GoogleApiOrientation>.toApiModel(): List<Orientation> = map { googleApiOrientation ->
    Orientation(
        id = googleApiOrientation.id ?: UNABLE,
        name = googleApiOrientation.name ?: UNABLE,
        tags = googleApiOrientation.tags ?: emptyList()
    )
}

private const val UNABLE = "[Unable to fetch]"
