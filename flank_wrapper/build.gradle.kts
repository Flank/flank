plugins {
    kotlin(Plugins.Kotlin.PLUGIN_JVM)
    application
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":common"))
    implementation(Dependencies.Fuel.CORE)
    testImplementation(Dependencies.JUNIT)
    testImplementation(Dependencies.TRUTH)
}

application {
    // Define the main class for the application.
    mainClass.set("com.github.flank.wrapper.MainKt")
}
