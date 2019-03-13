package ftl.doctor

import com.google.common.truth.Truth.assertThat
import ftl.args.AndroidArgs
import ftl.args.IosArgs
import ftl.test.util.FlankTestRunner
import java.nio.file.Paths
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(FlankTestRunner::class)
class DoctorTest {
    @Test
    fun androidDoctorTest() {
        val lint = Doctor.validateYaml(AndroidArgs, Paths.get("src/test/kotlin/ftl/fixtures/flank.local.yml"))
        assertThat(lint).isEmpty()
    }

    @Test
    fun androidDoctorTest2() {
        val lint = Doctor.validateYaml(
            AndroidArgs, """
hi: .
foo:
  bar: 1
gcloud:
  results-bucket: .
  record-video: .
  timeout: .
  async: .
  results-history-name: .

  app: .
  test: .
  auto-google-login: .
  use-orchestrator: .
  environment-variables:
    clearPackageData: .
  directories-to-pull:
  - .
  performance-metrics: .
  test-targets:
  - .
  device:
  - model: .
    version: .
    locale: .
    orientation: .
  two: .

flank:
  max-test-shards: 7
  repeat-tests: 8
  test-targets-always-run:
    - .
  three: .
  project: .
        """.trimIndent()
        )
        assertThat(lint).isEqualTo(
            """
Unknown top level keys: [hi, foo]
Unknown keys in gcloud -> [two]
Unknown keys in flank -> [three]

""".trimIndent()
        )
    }

    @Test
    fun androidDoctorTest3() {
        val lint = Doctor.validateYaml(
            AndroidArgs, """
gcloud:
  app: .
  test: .
flank:
  project: .
        """.trimIndent()
        )
        assertThat(lint).isEqualTo("")
    }

    @Test
    fun iosDoctorTest() {
        val lint = Doctor.validateYaml(IosArgs, Paths.get("src/test/kotlin/ftl/fixtures/flank.ios.yml"))
        assertThat(lint).isEmpty()
    }

    @Test
    fun iosDoctorTest2() {
        val lint = Doctor.validateYaml(
            IosArgs, """
hi: .
foo:
  bar: 1
gcloud:
  results-bucket: .
  record-video: .
  timeout: .
  async: .
  results-history-name: .

  test: .
  xctestrun-file: .
  device:
  - model: .
    version: .
    locale: .
    orientation: .
  two: .

flank:
  max-test-shards: .
  repeat-tests: .
  test-targets-always-run:
    - .
  test-targets:
    - .
  three: .
  project: .
""".trimIndent()
        )
        assertThat(lint).isEqualTo(
            """
Unknown top level keys: [hi, foo]
Unknown keys in gcloud -> [two]
Unknown keys in flank -> [three]

""".trimIndent()
        )
    }

    @Test
    fun iosDoctorTest3() {
        val lint = Doctor.validateYaml(
            IosArgs, """
gcloud:
  test: .
  xctestrun-file: .
flank:
  project: .
""".trimIndent()
        )
        assertThat(lint).isEqualTo("")
    }
}
