package ftl.run.platform.ios

import ftl.args.IosArgs
import ftl.args.yml.Type
import ftl.run.model.IosTestContext
import kotlinx.coroutines.flow.Flow

fun IosArgs.createIosTestContexts(): Flow<IosTestContext> = when (type) {
    Type.GAMELOOP -> createGameLoopTestContexts()
    Type.ROBO -> throw NotImplementedError()
    Type.XCTEST -> createXcTestContexts()
    else -> throw NotImplementedError()
}
