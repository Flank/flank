package ftl.adapter.google

import com.google.testing.model.IosVersion
import ftl.api.OsVersion

fun List<IosVersion>.toApiModel() = map {
    it.toApiModel()
}

private fun IosVersion.toApiModel() = OsVersion.Ios(id, majorVersion, minorVersion, supportedXcodeVersionIds, tags)
