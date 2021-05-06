package ftl.reports.util

import ftl.reports.util.ReportManager.endsWithTextWithOptionalSlashAtTheEnd
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class EndsWithTextWithOptionalSlashAtTheEndTest {

    @Test
    fun `should properly found end suffix matching text`() {
        // given
        val searchingSuffix = "test/link"

        // when
        assertTrue("www.flank.com/test/link".endsWithTextWithOptionalSlashAtTheEnd(searchingSuffix))
        assertTrue("www.flank.com/test/link/".endsWithTextWithOptionalSlashAtTheEnd(searchingSuffix))
        assertFalse("www.flank.com/test/link/2".endsWithTextWithOptionalSlashAtTheEnd(searchingSuffix))
        assertFalse("www.flank.com/test/link2".endsWithTextWithOptionalSlashAtTheEnd(searchingSuffix))
        assertFalse("www.flank.com/test/2/link/".endsWithTextWithOptionalSlashAtTheEnd(searchingSuffix))
    }
}
