package flank.exection.parallel.internal

import flank.exection.parallel.Tasks
import flank.exection.parallel.example.ExampleExecution
import flank.exection.parallel.example.errorAfterA
import flank.exection.parallel.example.produceA
import org.junit.Assert
import org.junit.Test

class UtilsKtTest {

    @Test
    fun find() {
        val expected: Tasks<ExampleExecution.Context> = setOf(
            errorAfterA,
            produceA,
        )
        val actual = ExampleExecution.tasks.subgraph(setOf(errorAfterA))

        println(actual.map { it.signature.returns.javaClass.simpleName })
        Assert.assertEquals(expected, actual)
    }
}
