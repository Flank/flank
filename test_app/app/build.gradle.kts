import org.jetbrains.kotlin.config.KotlinCompilerVersion

plugins {
    id("com.android.application")
    id("kotlin-android")
    id("kotlin-android-extensions")
}

android {
    compileSdkVersion(28)
    defaultConfig {
        applicationId = "com.example.app"
        minSdkVersion(24)
        targetSdkVersion(28)
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "android.support.test.runner.AndroidJUnitRunner"
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }

    testOptions {
        animationsDisabled = true
        execution = "ANDROID_TEST_ORCHESTRATOR"
    }
}

dependencies {
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
    implementation(kotlin("stdlib-jdk8", KotlinCompilerVersion.VERSION))
    implementation("com.android.support:appcompat-v7:28.0.0-rc01")
    implementation("com.android.support.constraint:constraint-layout:1.1.2")

    val runnerVersion = "1.0.2-alpha1"
    val espressoVersion = "3.0.2-alpha1"

    androidTestUtil("com.android.support.test:orchestrator:$runnerVersion")
    androidTestImplementation("com.android.support.test.espresso:espresso-core:$espressoVersion")
    androidTestImplementation("com.android.support.test:runner:$runnerVersion")
    testImplementation("junit:junit:4.12")
}
