package flank.corellium.domain

import flank.exection.parallel.plantuml.generatePlanUml
import org.junit.Test
import java.io.File

class AndroidTestDiagram {

    @Test
    fun generate() {
        TestAndroid.run { generatePlanUml(execute - context.validate) }
    }

    @Test
    fun generateDevice() {
        generatePlanUml(
            tasks = TestAndroid.Device.execute,
            path = File("TestAndroid_Device").absolutePath + "-execute.puml",
            prefixToRemove = TestAndroid.javaClass.name
        )
    }
}
