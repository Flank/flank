package ftl.adapter.google

import com.google.api.services.testing.model.IosVersion
import ftl.api.OsVersion

fun List<IosVersion>.toApiModel() = map {
    it.toApiModel()
}

private fun IosVersion.toApiModel() = OsVersion.Ios(id, majorVersion, minorVersion, supportedXcodeVersionIds, tags)
