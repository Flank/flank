package ftl.adapter.google

import com.google.api.services.testing.model.AndroidModel
import com.google.api.services.testing.model.IosModel
import ftl.api.DeviceModel
import ftl.environment.orUnknown
import ftl.environment.orUnspecified

internal fun List<AndroidModel>.toAndroidApiModel(): List<DeviceModel.Android> = map { googleAndroidModel ->
    DeviceModel.Android(
        id = googleAndroidModel.id.orUnknown(),
        name = googleAndroidModel.name.orUnknown(),
        tags = googleAndroidModel.tags.orEmpty(),
        screenX = googleAndroidModel.screenX.orUnspecified(),
        screenY = googleAndroidModel.screenY.orUnspecified(),
        formFactor = googleAndroidModel.formFactor.orUnknown(),
        screenDensity = googleAndroidModel.screenDensity.orUnspecified(),
        supportedVersionIds = googleAndroidModel.supportedVersionIds.orEmpty(),
        form = googleAndroidModel.form.orUnknown(),
        brand = googleAndroidModel.brand.orUnknown(),
        codename = googleAndroidModel.codename.orUnknown(),
        manufacturer = googleAndroidModel.manufacturer.orUnknown(),
        thumbnailUrl = googleAndroidModel.thumbnailUrl.orUnknown(),
        supportedAbis = googleAndroidModel.supportedAbis.orEmpty(),
        lowFpsVideoRecording = googleAndroidModel.lowFpsVideoRecording ?: false
    )
}

internal fun List<IosModel>.toIosApiModel(): List<DeviceModel.Ios> = map { googleiOSModel ->
    DeviceModel.Ios(
        googleiOSModel.id.orEmpty(),
        googleiOSModel.name.orUnknown(),
        googleiOSModel.tags.orEmpty(),
        googleiOSModel.screenX.orUnspecified(),
        googleiOSModel.screenY.orUnspecified(),
        googleiOSModel.formFactor.orUnknown(),
        googleiOSModel.screenDensity.orUnspecified(),
        googleiOSModel.supportedVersionIds.orEmpty(),
        googleiOSModel.deviceCapabilities.orEmpty()
    )
}
