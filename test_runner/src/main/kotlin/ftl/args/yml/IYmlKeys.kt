package ftl.args.yml

import com.fasterxml.jackson.annotation.JsonProperty
import kotlin.reflect.KClass
import kotlin.reflect.KMutableProperty
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.memberProperties

interface IYmlKeys {
    val group: String
    val keys: List<String>

    object Group {
        const val FLANK = "flank"
        const val GCLOUD = "gcloud"
    }
}

val KClass<*>.ymlKeys
    get() = memberProperties
            .filterIsInstance<KMutableProperty<*>>()
            .mapNotNull { it.setter.findAnnotation<JsonProperty>() }
            .map { it.value }

fun mergeYmlKeys(
    vararg keys: IYmlKeys
): Map<String, List<String>> = keys
    .groupBy(IYmlKeys::group, IYmlKeys::keys)
    .mapValues { (_, keys) -> keys.flatten() }
