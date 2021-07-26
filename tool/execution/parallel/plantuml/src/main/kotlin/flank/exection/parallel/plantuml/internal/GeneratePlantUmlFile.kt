package flank.exection.parallel.plantuml.internal

import flank.exection.parallel.Tasks
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
