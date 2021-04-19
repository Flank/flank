package ftl.cli.firebase.test.ipblocks

import com.google.testing.model.Date
import com.google.testing.model.DeviceIpBlock
import ftl.client.google.deviceIPBlocks
import ftl.test.util.runMainCommand
import io.mockk.every
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.contrib.java.lang.system.SystemOutRule

class IPBlocksListCommandTest {

    @get:Rule
    val out = SystemOutRule().enableLog().muteForSuccessfulTests() as SystemOutRule

    @Before
    fun setup() = mockkStatic("ftl.client.google.DeviceIpBlocksKt")

    @After
    fun tearDown() = unmockkAll()

    @Test
    fun `should print error message if no data was provided from FTL`() {
        val expected = "--Flank was unable to get data from TestLab--"
        every { deviceIPBlocks() } returns emptyList()

        out.clearLog()
        runMainCommand("ip-blocks", "list")

        val result = out.log.trim()

        assertEquals(expected, result)
    }

    @Test
    fun `should print ips for devices`() {
        val ips = listOf(
            DeviceIpBlock().apply {
                block = "1.1.1.1/1"
                form = "AnyForm"
                addedDate = Date().apply {
                    day = 1
                    month = 1
                    year = 1111
                }
            },
            DeviceIpBlock().apply {
                block = "2.2.2.2/2"
                form = "OtherForm"
                addedDate = Date().apply {
                    day = 12
                    month = 12
                    year = 1212
                }
            }
        )

        every { deviceIPBlocks() } returns ips
        val expected = """
            ┌───────────┬───────────┬────────────┐
            │   BLOCK   │   FORM    │ ADDED_DATE │
            ├───────────┼───────────┼────────────┤
            │ 1.1.1.1/1 │ AnyForm   │ 1111-01-01 │
            │ 2.2.2.2/2 │ OtherForm │ 1212-12-12 │
            └───────────┴───────────┴────────────┘
        """.trimIndent()
        out.clearLog()

        runMainCommand("ip-blocks", "list")

        val result = out.log.trim()
        assertEquals(expected, result)
    }

    @Test
    fun `should not fail when FTL returns null in any of values`() {
        val ips = listOf(
            DeviceIpBlock().apply {
                block = "1.1.1.1/1"
                form = "AnyForm"
                addedDate = Date().apply {
                    day = null
                    month = 1
                    year = 1111
                }
            },
            DeviceIpBlock().apply {
                block = null
                form = "MissingIpForm"
                addedDate = Date().apply {
                    day = 12
                    month = 2
                    year = 1212
                }
            },
            DeviceIpBlock().apply {
                block = "2.2.2.2/2"
                form = "OtherForm"
                addedDate = Date().apply {
                    day = 12
                    month = null
                    year = 1212
                }
            },
            DeviceIpBlock().apply {
                block = "3.3.3.3/4"
                form = "FunnyForm"
                addedDate = Date().apply {
                    day = 8
                    month = 2
                    year = null
                }
            },
            DeviceIpBlock().apply {
                block = "4.4.4.4/4"
                form = null
                addedDate = Date().apply {
                    day = 8
                    month = 2
                    year = 1523
                }
            }
        )

        every { deviceIPBlocks() } returns ips
        val expected = """
            ┌───────────────────┬───────────────────┬───────────────────┐
            │       BLOCK       │       FORM        │    ADDED_DATE     │
            ├───────────────────┼───────────────────┼───────────────────┤
            │ 1.1.1.1/1         │ AnyForm           │ [Unable to fetch] │
            │ [Unable to fetch] │ MissingIpForm     │ 1212-02-12        │
            │ 2.2.2.2/2         │ OtherForm         │ [Unable to fetch] │
            │ 3.3.3.3/4         │ FunnyForm         │ [Unable to fetch] │
            │ 4.4.4.4/4         │ [Unable to fetch] │ 1523-02-08        │
            └───────────────────┴───────────────────┴───────────────────┘
        """.trimIndent()
        out.clearLog()

        runMainCommand("ip-blocks", "list")

        val result = out.log.trim()
        assertEquals(expected, result)
    }
}
