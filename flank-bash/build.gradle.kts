plugins {
    kotlin("jvm")
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-scripting-common:${Versions.KOTLIN}")
    implementation("org.jetbrains.kotlin:kotlin-scripting-jvm:${Versions.KOTLIN}")
    implementation("org.jetbrains.kotlin:kotlin-scripting-jvm-host:${Versions.KOTLIN}")
    implementation("org.jetbrains.kotlin:kotlin-scripting-dependencies:${Versions.KOTLIN}")
    implementation("org.apache.ivy:ivy:2.5.0")
}
