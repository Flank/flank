tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

repositories {
    mavenCentral()
}

dependencies {
    // https://mvnrepository.com/artifact/com.google.api-client/google-api-client
    compile("com.google.api-client:google-api-client:1.23.0")
}
