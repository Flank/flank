package ftl.presentation

import com.google.common.truth.Truth.assertThat
import io.mockk.mockk
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.runTest
import org.junit.Test

class RunStateTest {

    @Test
    // https://github.com/Kotlin/kotlinx.coroutines/blob/d737da6f109784b67977af579fe3274c250857c9/kotlinx-coroutines-test/MIGRATION.md#replace-runblockingtest-with-runtestunconfinedtestdispatcher
    // Replace runBlockingTest with runTest(UnconfinedTestDispatcher())
    fun `should publish new run state`() = runTest(UnconfinedTestDispatcher()) {
        // given
        val testItem = object : RunState {}

        // when
        val actual = async { runSharedFlow.first() }
        testItem.publish()

        // then
        assertThat(actual.await()).isEqualTo(testItem)
    }

    @Test
    fun `should observe run state and self cancel it`() {
        // given
        val testItem = mockk<RunState>()

        // when
        TestOutputClass().runBlockingWithObservingRunState {
            testItem.publish()
        }

        // then
        // self cancel
    }

    private class TestOutputClass : Output {
        override val out: Any.() -> Unit
            get() = {}
    }
}
