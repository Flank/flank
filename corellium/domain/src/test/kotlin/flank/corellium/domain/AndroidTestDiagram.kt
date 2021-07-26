package flank.corellium.domain

import flank.exection.parallel.plantuml.generatePlanUml
import org.junit.Test

class AndroidTestDiagram {

    @Test
    fun generate() {
        TestAndroid.run { generatePlanUml(execute - context.validate) }
    }
}
