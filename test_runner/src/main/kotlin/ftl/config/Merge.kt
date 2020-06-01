package ftl.config

operator fun <G : Config, F : Config> Config.Platform<G, F>.plus(other: Config.Platform<G, F>?) = copy(
    data = if (other?.data?.isNotBlank() == true) other.data else data
).apply {
    other?.let {
        common + other.common
        platform + other.platform
    }
}

private operator fun <G : Config, F : Config> Config.Partial<G, F>.plus(other: Config.Partial<G, F>) = apply {
    gcloud + other.gcloud
    flank + other.flank
}

private operator fun <C : Config> C.plus(other: Config) = apply {
    data += other.data
}
