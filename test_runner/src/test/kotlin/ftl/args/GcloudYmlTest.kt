package ftl.args

import com.google.common.truth.Truth.assertThat
import ftl.args.yml.GcloudYml
import ftl.test.util.FlankTestRunner
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(FlankTestRunner::class)
class GcloudYmlTest {

    @Test
    fun gcloudYml() {
        val gcloud = GcloudYml().gcloud
        gcloud.resultsBucket = "mockBucket"
        assertThat(gcloud.resultsBucket)
            .isEqualTo("mockBucket")

        gcloud.resultsBucket = "tmp"
        assertThat(gcloud.resultsBucket)
            .isEqualTo("tmp")
    }
}
