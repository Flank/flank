import com.gradle.enterprise.gradleplugin.GradleEnterprisePlugin
import com.gradle.scan.plugin.BuildScanPlugin
import java.io.ByteArrayOutputStream

/**
 * To publish scan within the build, run
 * ./gradlew build -I gradle/scans-init.gradle.kts
 */
initscript {
    val pluginVersion = "3.4.1"

    repositories {
        gradlePluginPortal()
    }
    dependencies {
        classpath("com.gradle:gradle-enterprise-gradle-plugin:${pluginVersion}")
    }
}

val isTopLevelBuild = gradle.parent == null

if (isTopLevelBuild) {
    beforeSettings {
        if (!pluginManager.hasPlugin("com.gradle.enterprise")) {
            pluginManager.apply(GradleEnterprisePlugin::class)
        }
        configureExtension(extensions["gradleEnterprise"])
    }
}

fun configureExtension(extension: Any) {
    extension.withGroovyBuilder {
        getProperty("buildScan").withGroovyBuilder {
            setProperty("termsOfServiceUrl", "https://gradle.com/terms-of-service")
            setProperty("termsOfServiceAgree", "yes")
            "publishAlways"()
            "background"() {
                val os = ByteArrayOutputStream()
                exec {
                    commandLine("git", "rev-parse", "--verify", "HEAD")
                    standardOutput = os
                }
                "value"("Git Commit ID", os.toString())
            }
        }
    }
}
