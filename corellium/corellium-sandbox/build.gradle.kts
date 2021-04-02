import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    application
    kotlin(Plugins.Kotlin.PLUGIN_JVM)
    id(Plugins.PLUGIN_SHADOW_JAR) version Versions.SHADOW
}

application {
    mainClassName = "flank.corellium.sandbox.ios.ExampleCorelliumRun"
}

repositories {
    jcenter()
    mavenCentral()
    maven(url = "https://kotlin.bintray.com/kotlinx")
}

tasks.withType<KotlinCompile> { kotlinOptions.jvmTarget = "1.8" }

dependencies {
    implementation(Dependencies.KOTLIN_COROUTINES_CORE)
    implementation(project(":corellium:corellium-client"))
}

val runExampleScript by tasks.registering(JavaExec::class) {
    dependsOn(tasks.build)
    classpath = sourceSets["main"].runtimeClasspath
    main = "flank.corellium.sandbox.ios.ExampleCorelliumRun"
}

val androidExampleJar by tasks.registering(Jar::class) {
    archiveClassifier.set("all")
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    archiveBaseName.set("android-example")
    manifest {
        attributes("Main-Class" to "flank.corellium.sandbox.android.AndroidExample")
    }
    from(
        configurations.runtimeClasspath
            .get()
            .map { if (it.isDirectory) it else zipTree(it) }
    )
    from(sourceSets.main.get().output)
}

val runAndroidExample by tasks.registering(Exec::class) {
    dependsOn(androidExampleJar)
    workingDir = project.rootDir
    commandLine("java", "-jar", "./corellium/corellium-sandbox/build/libs/android-example-all.jar")
}

file("./src/main/resources/corellium-config.properties").also { propFile ->
    if (!propFile.exists() && System.getenv("CI") == null)
        file("./src/main/resources/corellium-config.properties_template").copyTo(propFile)
}
