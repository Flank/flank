package flank.instrument.command

import org.junit.Assert.assertEquals
import org.junit.Test

class FormatAmInstrumentCommandKtTest {

    @Test
    fun test() {
        val expected = "am instrument -r -w" +
            " -e class com.package.Test1#testMethod1,com.package.Test2#testMethod1,com.package.Test2#testMethod2" +
            " -e package com.package.nested1,com.package.nested2 com.package/AnyRunner"

        val actual = formatAmInstrumentCommand(
            packageName = "com.package",
            testRunner = "AnyRunner",
            testCases = listOf(
                "class com.package.Test1#testMethod1",
                "class com.package.Test2#testMethod1",
                "class com.package.Test2#testMethod2",
                "package com.package.nested1",
                "package com.package.nested2",
            )
        )

        assertEquals(expected, actual)
    }
}
