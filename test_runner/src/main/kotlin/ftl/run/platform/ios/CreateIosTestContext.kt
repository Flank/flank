package ftl.run.platform.ios

import ftl.args.IosArgs
import ftl.args.yml.Type
import ftl.run.model.IosTestContext
import kotlinx.coroutines.flow.Flow

fun IosArgs.createIosTestContexts(): Flow<IosTestContext> = when (type) {
    Type.XCTEST -> createXcTestContexts()
    Type.GAMELOOP -> createGameLoopTestContexts()
    else -> throw NotImplementedError()
}
