package ftl.api

import ftl.adapter.GoogleLocalesFetch

val fetchLocales: Locale.Fetch get() = GoogleLocalesFetch

data class Locale(
    val id: String,
    val name: String,
    val region: String,
    val tags: List<String>,
) {

    data class Identity(
        val projectId: String,
        val platform: Platform,
        val locale: String? = null,
    )

    interface Fetch : (Identity) -> List<Locale>
}
