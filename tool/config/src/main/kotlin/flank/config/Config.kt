package flank.config

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import java.io.File

/**
 * Abstraction for configuration structures
 */
interface ConfigMap {
    val data: MutableMap<String, Any?>
}

/**
 * Merge the configurations and accumulate results in first.
 * The next value overwrites the preceding one.
 *
 * @param first The accumulator.
 * @param others Other configuration to accumulate in first.
 * @return The [first] parameter with accumulated values.
 */
fun <C : ConfigMap> merge(first: C, vararg others: C): C =
    others.fold(first) { acc, config -> acc.apply { data += config.data } }

/**
 * Factory method for the empty configuration map with null as default value
 */
fun emptyConfigMap(): MutableMap<String, Any?> =
    mutableMapOf<String, Any?>().withDefault { null }

/**
 * Load the yaml configuration file as structure.
 */
inline fun <reified T> loadYaml(path: String): T =
    yamlMapper.readValue(File(path), T::class.java)

val yamlMapper: ObjectMapper by lazy {
    ObjectMapper(YAMLFactory()).registerKotlinModule()
}
