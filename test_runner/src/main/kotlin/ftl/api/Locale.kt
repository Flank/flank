package ftl.api

import ftl.adapter.GoogleLocalesFetch

val fetchLocales: Locale.Fetch get() = GoogleLocalesFetch

data class Locale(
    val id: String,
    val name: String,
    val region: String,
    val tags: List<String>,
) {

    data class Available(
        val locale: String,
        val result: List<Locale>
    )

    data class Identity(
        val projectId: String,
        val platform: Platform
    )

    interface Fetch : (Identity) -> List<Locale>
}
