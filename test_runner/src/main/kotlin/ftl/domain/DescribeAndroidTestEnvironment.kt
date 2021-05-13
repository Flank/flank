package ftl.domain

import ftl.api.fetchAndroidTestEnvironment
import ftl.args.AndroidArgs
import ftl.presentation.Output
import java.nio.file.Paths

interface DescribeAndroidTestEnvironment : Output {
    val configPath: String
}

fun DescribeAndroidTestEnvironment.invoke() {
    fetchAndroidTestEnvironment(AndroidArgs.loadOrDefault(Paths.get(configPath)).project).out()
}
