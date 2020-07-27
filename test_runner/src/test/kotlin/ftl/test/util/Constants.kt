package ftl.test.util

import ftl.args.AndroidArgs
import ftl.test.util.TestHelper.getPath

internal val mixedConfigYaml = getPath("src/test/kotlin/ftl/fixtures/test_app_cases/flank-multiple-mixed.yml")
internal val differentDevicesTypesYaml = getPath("src/test/kotlin/ftl/fixtures/test_app_cases/flank-single-multi-device-success.yml")
internal val differentDevicesTypesPhysicalLimitYaml = getPath("src/test/kotlin/ftl/fixtures/test_app_cases/flank-single-device-limit-success.yml")
internal val ios2ConfigYaml = getPath("src/test/kotlin/ftl/fixtures/flank2.ios.yml")
internal val defaultTestTimeout = AndroidArgs.default().testTimeout
