package flank.scripts.ops.firebase

import com.google.common.truth.Truth.assertThat
import flank.scripts.FuelTestRunner
import org.junit.Test
import org.junit.runner.RunWith
import java.nio.file.Files

@RunWith(FuelTestRunner::class)
class SaveServiceAccountTest {

    @Test
    fun `Should download service account`() {
        // given
        val testPath = Files.createTempFile("xxx", ".json")
        val testLink = "http://test.account.service"

        // when
        saveServiceAccount(testLink, testPath)

        // then
        assertThat(testPath.toFile().readText()).isEqualTo(testContent)
    }

    @Test
    fun `Should store service account`() {
        // given
        val testPath = Files.createTempFile("xxx", ".json")
        val storeTestContent = "$testContent store"

        // when
        saveServiceAccount(storeTestContent, testPath)

        // then
        assertThat(testPath.toFile().readText()).isEqualTo(storeTestContent)
    }

    @Test
    fun `Should copy service account`() {
        // given
        val testPath = Files.createTempFile("xxx", ".json")
        val fileTestContent = "$testContent file"
        val serviceAccount = Files.createTempFile("account", ".json").apply {
            toFile().writeText(fileTestContent)
        }

        // when
        saveServiceAccount(serviceAccount.toString(), testPath)

        // then
        println(testPath.toFile().readText())
        assertThat(testPath.toFile().readText()).isEqualTo(fileTestContent)
    }
}

internal const val testContent = "service @ccount"
