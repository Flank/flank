package ftl.util

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class MatrixStateTest {

    @Test
    fun matrixState_validStates() {
        listOf(MatrixState.VALIDATING, MatrixState.PENDING, MatrixState.RUNNING).forEach { state ->
            assertThat(MatrixState.completed(state)).isFalse()
        }

        assertThat(MatrixState.completed(MatrixState.FINISHED)).isTrue()
    }
}
