package utils

fun getPropertyAsList(key: String) = System.getProperty(key).orEmpty().run {
    if (isBlank()) emptyList()
    else split(",")
}
