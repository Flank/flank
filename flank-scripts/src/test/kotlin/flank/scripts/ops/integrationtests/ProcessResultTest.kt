package flank.scripts.ops.integrationtests

import com.google.common.truth.Truth.assertThat
import flank.common.normalizeLineEnding
import flank.scripts.FuelTestRunner
import flank.scripts.cli.integrationtests.ProcessResultCommand
import flank.scripts.ops.integrationtests.common.ITResult
import flank.scripts.ops.integrationtests.common.IntegrationResultContext
import flank.scripts.ops.integrationtests.common.closeIssue
import flank.scripts.ops.integrationtests.common.createNewIssue
import flank.scripts.ops.integrationtests.common.postComment
import kotlinx.coroutines.runBlocking
import org.junit.Rule
import org.junit.Test
import org.junit.contrib.java.lang.system.SystemOutRule
import org.junit.runner.RunWith

@RunWith(FuelTestRunner::class)
class ProcessResultTest {

    @get:Rule
    val output: SystemOutRule = SystemOutRule().enableLog()

    private val ctx: IntegrationResultContext
        get() = IntegrationResultContext(
            result = ITResult.FAILURE,
            token = "success",
            runState = dummyITRunState,
            runID = "123abc",
            lastRun = "2000-10-10T12:33:17Z",
            openedIssue = null
        )

    @Test
    fun `should create new issue for failed result`() {
        runBlocking {
            ctx.createNewIssue()

            assertThat(output.log.trim().normalizeLineEnding()).contains(issueCreated)
        }
    }

    @Test
    fun `should post new comment`() {
        runBlocking {
            ctx.copy(openedIssue = 123).postComment()

            assertThat(output.log.trim().normalizeLineEnding()).contains(commentPosted)
        }
    }

    @Test
    fun `should close issue`() {
        runBlocking {
            ctx.copy(openedIssue = 123, result = ITResult.SUCCESS).closeIssue()

            assertThat(output.log.trim().normalizeLineEnding()).contains(issueClosed)
        }
    }

    @Test
    fun `should post new comment -- missing url`() {
        ProcessResultCommand.main(
            listOf(
                "--github-token=success",
                "--rr={}",
                "--global-result=failure",
                "--run-id=123"
            )
        )
        assertThat(output.log.trim().normalizeLineEnding()).contains(missingUrl)
    }
}

private val issueCreated = """
** Creating new issue
{
    "title": "Full Suite integration tests failed on master",
    "body": "### Integration Test failed on master",
    "labels": [
        "IT_Failed",
        "bug"
    ]
}
** Issue created:
     url:    https://bla.org
     number: 123 
** Comment posted
{
    "body": "### Full suite IT run :x: FAILED :x:\n**Timestamp:** 2000-10-10 12:33:17\n**Job run:** [123abc](https://github.com/Flank/flank/actions/runs/123abc)\n**Windows status:**  FAILURE  - url: N/A\n**Linux status:**    FAILURE  - url: N/A\n**MacOs status:**    FAILURE  - url: N/A\n|commit SHA|PR|\n|---|:---:|\n|aaaaaaaaa|[feat: new Feature](www.pull.request)\n"
}
""".trimIndent()

private val commentPosted = """
** Comment posted
{
    "body": "### Full suite IT run :x: FAILED :x:\n**Timestamp:** 2000-10-10 12:33:17\n**Job run:** [123abc](https://github.com/Flank/flank/actions/runs/123abc)\n**Windows status:**  FAILURE  - url: N/A\n**Linux status:**    FAILURE  - url: N/A\n**MacOs status:**    FAILURE  - url: N/A\n|commit SHA|PR|\n|---|:---:|\n|aaaaaaaaa|[feat: new Feature](www.pull.request)\n"
}
""".trimIndent()

private val issueClosed = """
** Comment posted
{
    "body": "### Full suite IT run :white_check_mark: SUCCEEDED :white_check_mark:\n**Timestamp:** 2000-10-10 12:33:17\n**Job run:** [123abc](https://github.com/Flank/flank/actions/runs/123abc)\n**Windows url:** N/A\n**Linux url:** N/A\n**MacOs url:** N/A\n**Closing issue**"
}
** Closing issue
""".trimIndent()

private val missingUrl = """
** Parameters:
     global run result: FAILURE
     run state: {
}
     runID:  123
** Last workflow run:
     name: any-name
     last run: 2020-12-10T09:51:56.797534Z
     url: http://workflow.run/123
** Issue found: www.pull.request
** Comment posted
{
    "body": "### Full suite IT run :x: FAILED :x:\n**Timestamp:** 2020-12-10 09:51:56\n**Job run:** [123](https://github.com/Flank/flank/actions/runs/123)\n**Windows status:**  FAILURE  - url: N/A\n**Linux status:**    FAILURE  - url: N/A\n**MacOs status:**    FAILURE  - url: N/A\n|commit SHA|PR|\n|---|:---:|\n|aaaaaaaaa|[feat: new Feature](www.pull.request)\n"
}
""".trimIndent()
