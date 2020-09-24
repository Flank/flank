import org.junit.Assume.assumeFalse

// TODO move to shared module
fun skipIfWindows() {
    assumeFalse(isWindows)
}

private val isWindows get() = System.getProperty("os.name")?.toLowerCase().orEmpty().indexOf("win") >= 0
