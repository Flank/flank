package flank.scripts

import com.github.kittinunf.fuel.core.FuelManager
import org.junit.runners.BlockJUnit4ClassRunner
import org.junit.runners.model.Statement

class FuelTestRunner(klass: Class<*>) : BlockJUnit4ClassRunner(klass) {

    override fun withBeforeClasses(statement: Statement?): Statement {
        startMockClient()
        return super.withBeforeClasses(statement)
    }

    private fun startMockClient() {
        FuelManager.instance.client = FuelMockServer()
    }
}
