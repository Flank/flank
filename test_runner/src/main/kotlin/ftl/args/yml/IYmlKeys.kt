package ftl.args.yml

interface IYmlKeys {
    val group: String
    val keys: List<String>

    object Group {
        const val FLANK = "flank"
        const val GCLOUD = "gcloud"
    }
}

fun mergeYmlKeys(
    vararg keys: IYmlKeys
): Map<String, List<String>> = keys
    .groupBy(IYmlKeys::group, IYmlKeys::keys)
    .mapValues { (_, keys) -> keys.flatten() }
