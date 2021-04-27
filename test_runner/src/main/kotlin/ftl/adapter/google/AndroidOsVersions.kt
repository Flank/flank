package ftl.adapter.google

import com.google.testing.model.AndroidVersion
import com.google.testing.model.Date
import com.google.testing.model.Distribution
import ftl.api.OsVersion

fun List<AndroidVersion>.toApiModel() = map {
    it.toApiModel()
}

private fun AndroidVersion.toApiModel() = OsVersion.Android(
    apiLevel,
    codeName,
    distribution?.toApiModel(),
    id,
    releaseDate?.toApiModel(),
    tags,
    versionString
)

private fun Distribution.toApiModel() = OsVersion.Distribution(marketShare, measurementTime)

private fun Date.toApiModel() = OsVersion.Date(day, month, year)
