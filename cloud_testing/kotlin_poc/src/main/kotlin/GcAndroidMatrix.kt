import com.google.common.collect.Lists
import com.google.testing.model.AndroidMatrix

object GcAndroidMatrix {

    fun build(
            modelIds: String, versionIds: String, locales: String, orientations: String): AndroidMatrix {
        val androidMatrix = AndroidMatrix()
        androidMatrix.androidModelIds = Lists.newArrayList(modelIds)
        androidMatrix.androidVersionIds = Lists.newArrayList(versionIds)
        androidMatrix.locales = Lists.newArrayList(locales)
        androidMatrix.orientations = Lists.newArrayList(orientations)
        return androidMatrix
    }
}
