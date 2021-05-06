package ftl.domain

import ftl.api.fetchIosTestEnvironment
import ftl.args.IosArgs
import ftl.presentation.Output
import java.nio.file.Paths

interface DescribeIosTestEnvironment : Output {
    val configPath: String
}

operator fun DescribeIosTestEnvironment.invoke() {
    val projectId = IosArgs.loadOrDefault(Paths.get(configPath)).project
    fetchIosTestEnvironment(projectId).out()
}
