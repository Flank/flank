package ftl.run.platform.ios

import ftl.args.IosArgs
import ftl.args.isXcTest
import ftl.run.model.IosTestContext
import kotlinx.coroutines.flow.Flow

fun IosArgs.createIosTestContexts(): Flow<IosTestContext> =
    if (isXcTest) createXcTestContexts()
    else createGameloopTestContexts()
