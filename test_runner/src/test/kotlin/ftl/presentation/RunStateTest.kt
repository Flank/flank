package ftl.presentation

import com.google.common.truth.Truth.assertThat
import io.mockk.mockk
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Test

class RunStateTest {

    @Test
    fun `should publish new run state`() = runBlockingTest {
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
