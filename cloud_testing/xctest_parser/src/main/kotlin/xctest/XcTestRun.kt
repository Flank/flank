package xctest

data class TestEntry(
        val key: String,
        val dict: HashMap<String, Any>
)

data class XcTestRun(
        val version: String,
        val dict: TestEntry
)
