abstract class Versions {
    public static final String KOTLIN = "1.2.30";
}

abstract class Plugins {
    public static final String KOTLIN = "org.jetbrains.kotlin:kotlin-gradle-plugin:" + Versions.KOTLIN;
}

abstract class Libs {
    public static final String KOTLIN_STD_LIB = "org.jetbrains.kotlin:kotlin-stdlib:" + Versions.KOTLIN;
    public static final String KOTLIN_REFLECT = "org.jetbrains.kotlin:kotlin-reflect:" + Versions.KOTLIN;
}
