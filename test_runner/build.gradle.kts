import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.jetbrains.kotlin.gradle.dsl.Coroutines

group = "test_runner"
version = "SNAPSHOT"

plugins {
    application
    jacoco
    kotlin("jvm") version Versions.KOTLIN
    // https://github.com/JLLeitschuh/ktlint-gradle/releases
    // ./gradlew ktlintFormat
    // ./gradlew ktlintCheck
    // ./gradlew ktlintApplyToIdea
    id("org.jlleitschuh.gradle.ktlint") version "6.3.1"
}

// http://www.eclemma.org/jacoco/
jacoco {
    toolVersion = "0.8.2"
}

// Html report created in ./build/reports/jacoco/test/html/index.html
// ./gradlew clean check jacocoTestReport
tasks.withType<JacocoReport> {
    doFirst {
        // Broken until issue in Kotlin DSL is resolved
        // https://github.com/gradle/kotlin-dsl/issues/1265
//        classDirectories = fileTree("build/classes/kotlin/main").apply {
//            // runBlocking {} produces generated Kotlin code. Ignore that.
//            exclude("**/*\$run$1.class")
//        }

        reports {
            html.isEnabled = true
            xml.isEnabled = true
        }
    }
}

tasks.withType<KotlinCompile>().configureEach {
    // https://devcenter.bitrise.io/builds/available-environment-variables/
    val runningOnBitrise = System.getenv("BITRISE_IO") != null
    kotlinOptions.allWarningsAsErrors = runningOnBitrise
}

apply {
    plugin("kotlin")
}

application {
    mainClassName = "main"
}

repositories {
    maven(url = "http://dl.bintray.com/kotlin/ktor")
    maven(url = "https://dl.bintray.com/kotlin/kotlinx")
    jcenter()
}

// Output full test results to console
// Avoids having to read the HTML report
tasks.withType<Test> {
    testLogging {
        events("passed", "skipped", "failed")
        exceptionFormat = TestExceptionFormat.FULL
    }
}

dependencies {
    compile(Libs.KOTLIN_STD_LIB)
    compile(Libs.KOTLIN_COROUTINES_CORE)

    // https://github.com/remkop/picocli/releases
    compile("info.picocli:picocli:3.7.0")
    compile("com.fasterxml.jackson.dataformat:jackson-dataformat-xml:2.9.6")
    compile("com.fasterxml.woodstox:woodstox-core:5.1.0")

    // https://github.com/google/gson/releases
    compile("com.google.code.gson:gson:2.8.5")

    // https://github.com/3breadt/dd-plist/releases
    compile("com.googlecode.plist:dd-plist:1.21")

    // https://search.maven.org/search?q=a:google-cloud-storage%20g:com.google.cloud
    compile("com.google.cloud:google-cloud-storage:1.49.0")

    // https://search.maven.org/search?q=a:google-api-services-toolresults%20g:com.google.apis
    compile("com.google.apis:google-api-services-toolresults:v1beta3-rev20181015-1.26.0")

    // https://github.com/linkedin/dex-test-parser/releases
    compile("com.linkedin.dextestparser:parser:2.0.0")

    // NOTE: iOS support isn't in the public artifact. Use testing jar generated from the private gcloud CLI json
    // https://search.maven.org/#search%7Cga%7C1%7Cg%3A%22com.google.apis%22%20AND%20a%3A%22google-api-services-testing%22
    // compile("com.google.apis:google-api-services-testing:v1-rev30-1.23.0")
    compile(project("test_api"))

    // yaml config
    // https://search.maven.org/search?q=a:jackson-databind%20g:com.fasterxml.jackson.core
    compile("com.fasterxml.jackson.core:jackson-databind:${Versions.JACKSON}")
    compile("com.fasterxml.jackson.module:jackson-module-kotlin:${Versions.JACKSON}")
    compile("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:${Versions.JACKSON}")
    compile("com.fasterxml.jackson.dataformat:jackson-dataformat-xml:${Versions.JACKSON}")
    compile("com.fasterxml.woodstox:woodstox-core:5.1.0")

    // https://github.com/jhy/jsoup/releases
    testImplementation("org.jsoup:jsoup:1.11.3")
    testImplementation(Libs.JUNIT)
    // https://github.com/google/truth/releases
    testImplementation("com.google.truth:truth:0.42")
    // https://github.com/stefanbirkner/system-rules/releases
    testImplementation("com.github.stefanbirkner:system-rules:1.18.0")

    // https://github.com/ktorio/ktor/releases
    val ktorVersion = "1.0.0-beta-4"
    testImplementation("io.ktor:ktor-server-core:$ktorVersion")
    testImplementation("io.ktor:ktor-server-netty:$ktorVersion")
    testImplementation("io.ktor:ktor-gson:$ktorVersion")

    // https://github.com/qos-ch/logback/releases
    testImplementation("ch.qos.logback:logback-classic:1.2.3")
    // mockito-inline is used to mock final classes
    // https://github.com/mockito/mockito/releases
    testImplementation("org.mockito:mockito-inline:2.23.0")
    // https://github.com/square/okhttp/releases
    testImplementation("com.squareup.okhttp3:okhttp:3.11.0")

    // todo: move to testImplementation once DI is implemented https://github.com/TestArmada/flank/issues/248
    // https://search.maven.org/search?q=a:google-cloud-nio%20g:com.google.cloud
    compile("com.google.cloud:google-cloud-nio:0.67.0-alpha")
}

// Fix Exception in thread "main" java.lang.NoSuchMethodError: com.google.common.hash.Hashing.crc32c()Lcom/google/common/hash/HashFunction;
// https://stackoverflow.com/a/45286710
configurations.all {
    resolutionStrategy {
        force("com.google.guava:guava:23.6-jre")
        force(Libs.KOTLIN_REFLECT)
        exclude(group = "com.google.guava", module = "guava-jdk5")
    }
}

task("fatJar", type = Jar::class) {
    baseName = "flank"
    manifest {
        attributes.apply {
            put("Main-Class", "ftl.Main")
        }
    }
    // https://github.com/gradle/kotlin-dsl/issues/1082#issuecomment-433035138
    from(configurations.runtime.get().map { file ->
        if (file.isDirectory) file else zipTree(file)
    })
    with(tasks["jar"] as CopySpec)
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

// https://github.com/gradle/kotlin-dsl/blob/master/samples/task-dependencies/build.gradle.kts#L41
// https://github.com/codecov/example-gradle/blob/master/build.gradle#L25
tasks["check"].dependsOn(tasks["jacocoTestReport"])
