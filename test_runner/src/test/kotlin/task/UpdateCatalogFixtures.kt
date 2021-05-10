package task

import com.google.api.client.json.GenericJson
import ftl.client.google.GcTesting
import java.nio.file.Files
import java.nio.file.Paths

object UpdateCatalogFixtures {
    private const val root = "./test_runner/src/test/kotlin/ftl/fixtures/"

    private fun write(fileName: String, content: GenericJson) {
        Files.write(Paths.get(root, fileName), content.toPrettyString().toByteArray())
    }

    @JvmStatic
    @Suppress("UnusedPrivateMember")
    fun main(args: Array<String>) {
        val androidCatalog = GcTesting.get.testEnvironmentCatalog().get("android").execute().androidDeviceCatalog
        write("android_catalog.json", androidCatalog)

        val iosCatalog = GcTesting.get.testEnvironmentCatalog().get("ios").execute().iosDeviceCatalog
        write("ios_catalog.json", iosCatalog)
    }
}
