import com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask

// Fix Exception in thread "main" java.lang.NoSuchMethodError: com.google.common.hash.Hashing.crc32c()Lcom/google/common/hash/HashFunction;
// https://stackoverflow.com/a/45286710
configurations.all {
    resolutionStrategy {
        force("com.google.guava:guava:25.1-jre")
        force(Dependencies.KOTLIN_REFLECT)
        exclude(group = "com.google.guava", module = "guava-jdk5")
    }
}

plugins {
    kotlin(Plugins.Kotlin.PLUGIN_JVM) version Versions.KOTLIN
    id(Plugins.DETEKT_PLUGIN) version Versions.DETEKT
    id(Plugins.BEN_MANES_PLUGIN) version Versions.BEN_MANES
}

repositories {
    jcenter()
    mavenCentral()
}

subprojects {
    afterEvaluate {
        if (tasks.findByName("detekt") != null) {
            tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
                dependsOn(tasks["detekt"])
            }
        }
    }
}

tasks.named("dependencyUpdates", DependencyUpdatesTask::class.java).configure {

    gradleReleaseChannel = "release-candidate"

    fun isStable(version: String) = listOf("RELEASE", "FINAL", "GA")
            .any { version.toUpperCase().contains(it) } || "^[0-9,.v-]+(-r)?$".toRegex().matches(version)

    fun isNonStable(version: String) = isStable(version).not()

    resolutionStrategy {
        componentSelection {
            all {

                if (candidate.group == "com.google.apis" &&
                    candidate.module == "google-api-services-toolresults" &&
                    !candidate.version.startsWith("v1beta3-")
                ) {
                    reject("com.google.apis:google-api-services-toolresults should use beta only")
                }

                if (isNonStable(candidate.version) && isStable(currentVersion)) {
                    reject("Release candidate")
                }
            }
        }
    }
}
