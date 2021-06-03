package flank.exection.parallel

import flank.exection.parallel.example.ExampleExecution
import flank.exection.parallel.example.invoke
import org.junit.Test
import java.text.SimpleDateFormat

class ParallelTest {

    @Test
    fun execute() {
        val result = object : ExampleExecution.Context {
            override val args = ExampleExecution.Args(
                wait = 50,
                a = 5,
                b = 8,
                c = 13,
            )
            override val out: Output = {
                when (this) {
                    is Parallel.Event -> println("${format.format(timestamp)} - ${type::class.simpleName}: $data")
                    else -> Unit
                }
            }
            private val format = SimpleDateFormat("yyyy.MM.dd HH:mm:ss.SSS")
        }.invoke()

        result.forEach { println(it) }
    }
}
