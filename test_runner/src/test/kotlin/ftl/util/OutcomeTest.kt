package ftl.util

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class OutcomeTest {

    @Test
    fun `outcome isNotEmpty`() {
        assertThat(StepOutcome.failure).isNotEmpty()
        assertThat(StepOutcome.success).isNotEmpty()
        assertThat(StepOutcome).isNotNull()
    }
}
