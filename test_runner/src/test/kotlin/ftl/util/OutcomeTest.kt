package ftl.util

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class OutcomeTest {

    @Test
    fun outcome_isNotEmpty() {
        assertThat(Outcome.failure).isNotEmpty()
        assertThat(Outcome.success).isNotEmpty()
        assertThat(Outcome).isNotNull()
    }
}
