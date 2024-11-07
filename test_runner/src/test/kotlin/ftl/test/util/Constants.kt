package ftl.test.util

import ftl.args.AndroidArgs
import ftl.test.util.TestHelper.getPath

internal val mixedConfigYaml = getPath("src/test/kotlin/ftl/fixtures/test_app_cases/flank-multiple-mixed.yml")
internal val ios2ConfigYaml = getPath("src/test/kotlin/ftl/fixtures/flank2.ios.yml")
internal val defaultTestTimeout = AndroidArgs.default().testTimeout
internal val parameterizedConfigYaml = getPath("src/test/kotlin/ftl/fixtures/test_app_cases/flank-parameterized-config.yml")

