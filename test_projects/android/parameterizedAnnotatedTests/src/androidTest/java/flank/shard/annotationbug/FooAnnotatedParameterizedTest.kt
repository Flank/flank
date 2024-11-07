package flank.shard.annotationbug

import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized

@RunWith(Parameterized::class)
class FooAnnotatedParameterizedTest {
    @Test
    @FooAnnotation
    fun useAppContext() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        Assert.assertEquals("flank.shard.annotationbug", appContext.packageName)
    }
}