package flank.execution.parallel.plantuml.internal

import flank.execution.parallel.Tasks
import java.io.File

internal fun generatePlanUmlFile(
    tasks: Tasks,
    path: String,
    prefixToRemove: String,
): File {
    val plant = generatePlantUmlString(tasks, prefixToRemove)
    println(plant)
    return File(path).apply { writeText(plant) }
}
