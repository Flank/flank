package ftl.gc

import com.google.api.services.testing.model.AndroidMatrix
import ftl.config.Devices

object GcAndroidMatrix {

    fun build(deviceList: List<Devices>): AndroidMatrix {
        val androidMatrix = AndroidMatrix()
        androidMatrix.androidModelIds = deviceList.map { it.model }
        androidMatrix.androidVersionIds = deviceList.map { it.version }
        androidMatrix.locales = deviceList.map { it.locale }
        androidMatrix.orientations = deviceList.map { it.orientation }
        return androidMatrix
    }
}
