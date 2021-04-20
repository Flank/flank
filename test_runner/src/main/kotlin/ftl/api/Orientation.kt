package ftl.api

import ftl.adapter.GoogleOrientationFetch

val fetchOrientation: Orientation.Fetch get() = GoogleOrientationFetch

data class Orientation(
    val id: String,
    val name: String,
    val tags: List<String>,
) {
    interface Fetch : (String, Platform) -> List<Orientation>
}

enum class Platform {
    ANDROID,
    IOS
}
