package ftl.args

import com.google.cloud.ServiceOptions
import com.google.common.truth.Truth.assertThat
import flank.common.defaultCredentialPath
import ftl.config.FtlConstants
import ftl.util.getGACPathOrEmpty
import io.mockk.every
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import org.junit.After
import org.junit.AfterClass
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.File

class FetchProjectIdTest {

    // mock server is not torn down between test classes
    // this is workaround until better solution is implemented
    companion object {
        @JvmStatic
        @BeforeClass
        fun noUseMock() {
            FtlConstants.useMock = false
        }

        @JvmStatic
        @AfterClass
        fun useMock() {
            FtlConstants.useMock = true
        }
    }

    @get:Rule
    val folder = TemporaryFolder()

    private lateinit var gac: File

    private lateinit var def: File

    @Before
    fun setup() {
        gac = folder.newFile("gap.json").also { it.writeText("""{"project_id": "id_from_gac"}""") }
        def = folder.newFile("def.json").also { it.writeText("""{"project_id": "id_from_def"}""") }
    }

    @After
    fun teardown() = unmockkAll()

    @Test
    fun `should fetch project id from GCLOUD_APPLICATION_CREDENTIALS`() {
        mockkStatic("ftl.util.Utils") {
            every { getGACPathOrEmpty() } returns gac.absolutePath.toString()
            assertThat(ArgsHelper.getDefaultProjectIdOrNull()).isEqualTo("id_from_gac")
        }
    }

    @Test
    fun `should fetch project id from default credentials`() {
        mockkStatic(
            "ftl.util.Utils",
            "flank.common.FilesKt",
            "com.google.cloud.ServiceOptions"
        ) {
            every { defaultCredentialPath } returns def.toPath()
            every { getGACPathOrEmpty() } returns ""
            every { ServiceOptions.getDefaultProjectId() } returns null
            assertThat(ArgsHelper.getDefaultProjectIdOrNull()).isEqualTo("id_from_def")
        }
    }
}
