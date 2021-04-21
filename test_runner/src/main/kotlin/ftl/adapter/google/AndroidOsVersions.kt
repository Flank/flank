package ftl.adapter.google

import com.google.testing.model.AndroidVersion
import com.google.testing.model.Distribution
import ftl.api.Date
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

private fun Distribution.toApiModel() = ftl.api.Distribution(marketShare, measurementTime)

private fun com.google.testing.model.Date.toApiModel() = Date(day, month, year)
