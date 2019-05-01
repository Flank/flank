package ftl.args.yml

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

data class AppTestPair(
    val app: String?,
    val test: String
)

/** Flank specific parameters for Android */
@JsonIgnoreProperties(ignoreUnknown = true)
class AndroidFlankYmlParams(
    @field:JsonProperty("additional-app-test-apks")
    val additionalAppTestApks: List<AppTestPair> = emptyList()
) {
    companion object : IYmlKeys {
        override val keys = listOf("additional-app-test-apks")
    }
}

@JsonIgnoreProperties(ignoreUnknown = true)
class AndroidFlankYml(
    @field:JsonProperty("flank")
    private val parsedFlank: AndroidFlankYmlParams? = AndroidFlankYmlParams()
) {
    val flank = parsedFlank ?: AndroidFlankYmlParams()

    companion object : IYmlMap {
        override val map = mapOf("flank" to AndroidFlankYmlParams.keys)
    }
}
