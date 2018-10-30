@SuppressWarnings("unused")
abstract class Versions {
    // match to Tools -> Kotlin -> Configure Kotlin Plugin Updates -> Update Channel: Stable
    public static final String KOTLIN = "1.3.0";
    // https://github.com/Kotlin/kotlinx.coroutines/releases
    public static final String KOTLIN_COROUTINES = "1.0.0";
    // https://github.com/FasterXML/jackson-core/releases
    public static final String JACKSON = "2.9.7";
}

@SuppressWarnings("unused")
abstract class Plugins {
    public static final String KOTLIN = "org.jetbrains.kotlin:kotlin-gradle-plugin:" + Versions.KOTLIN;
}

@SuppressWarnings("unused")
abstract class Libs {
    public static final String KOTLIN_STD_LIB = "org.jetbrains.kotlin:kotlin-stdlib:" + Versions.KOTLIN;
    public static final String KOTLIN_REFLECT = "org.jetbrains.kotlin:kotlin-reflect:" + Versions.KOTLIN;
    public static final String KOTLIN_COROUTINES_CORE = "org.jetbrains.kotlinx:kotlinx-coroutines-core:" + Versions.KOTLIN_COROUTINES;
    public static final String JUNIT = "junit:junit:4.12";
}
