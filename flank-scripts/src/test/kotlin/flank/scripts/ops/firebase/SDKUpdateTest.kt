package flank.scripts.ops.firebase

import com.google.common.truth.Truth.assertThat
import flank.common.normalizeLineEnding
import flank.scripts.FuelTestRunner
import flank.scripts.data.github.objects.GithubPullRequest
import flank.scripts.ops.firebase.common.createEpicIssue
import flank.scripts.ops.firebase.common.updateOpenedEpic
import flank.scripts.utils.parseToVersion
import kotlinx.coroutines.runBlocking
import org.junit.Rule
import org.junit.Test
import org.junit.contrib.java.lang.system.SystemOutRule
import org.junit.runner.RunWith

@RunWith(FuelTestRunner::class)
class SDKUpdateTest {

    @get:Rule
    val output: SystemOutRule = SystemOutRule().enableLog()

    private val ctx: SDKUpdateContext
        get() = SDKUpdateContext(
            newVersion = parseToVersion("321.0.0"),
            oldVersion = parseToVersion("319"),
            githubToken = "success",
            openedIssue = null,
            updatesLazy = { multi }
        )

    @Test
    fun `should create new epic with sub issues`() {
        runBlocking {
            ctx.createEpicIssue()

            assertThat(output.log.normalizeLineEnding()).contains(expectMulti)
        }
    }

    @Test
    fun `should update existing epic`() {
        runBlocking {
            ctx.copy(
                openedIssue = GithubPullRequest(
                    number = 123,
                    htmlUrl = "any.url",
                    assignees = emptyList(),
                    title = "outstanding title"
                )
            ).updateOpenedEpic()

            assertThat(output.log.normalizeLineEnding()).contains(expectOpened)
        }
    }
}

private val multi = """
    |## 321.0.0
    |### Firebase Test Lab
    |  * do sth funny once
    |  * do sth funny twice
    |### Firestore
    |  * aaaa
    |  * bbbb
    |## 320.1.1
    |### Kubernetes
    |  * kube1
    |  * kube2
    |### Firebase Test Lab
    |  * testlab 1
    |  * testlab 2
    |    * aaa
    |    * bbb
    |### Something Else
    |  * huge update
    |## 319
    |### Firebase Test Lab
    |  * should not be included
""".trimMargin().normalizeLineEnding()

private val expectMulti = """
    |** Create linked issues
    |** Issue created:
    |     url:    https://bla.org
    |     number: 123
    |** Issue created:
    |     url:    https://bla.org
    |     number: 123
    |** Issue created:
    |     url:    https://bla.org
    |     number: 123
    |** Issue created:
    |     url:    https://bla.org
    |     number: 123
    |** Issue created:
    |     url:    https://bla.org
    |     number: 123
    |** Issue created:
    |     url:    https://bla.org
    |     number: 123
""".trimMargin().normalizeLineEnding()

private val expectOpened = """
    |** Create linked issues
    |** Issue created:
    |     url:    https://bla.org
    |     number: 123
    |** Issue created:
    |     url:    https://bla.org
    |     number: 123
    |** Issue created:
    |     url:    https://bla.org
    |     number: 123
    |** Issue created:
    |     url:    https://bla.org
    |     number: 123
    |** Issue created:
    |     url:    https://bla.org
    |     number: 123
    |** Issue created:
    |     url:    https://bla.org
    |     number: 123
""".trimMargin().normalizeLineEnding()
