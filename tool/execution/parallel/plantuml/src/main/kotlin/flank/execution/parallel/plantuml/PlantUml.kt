package flank.execution.parallel.plantuml

import flank.execution.parallel.Tasks
import flank.execution.parallel.plantuml.internal.generatePlanUmlFile
import flank.execution.parallel.plantuml.internal.generatePlantUmlString
import java.io.File

/**
 * Generates plantuml file and saves on drive.
 *
 * @receiver Source object of execution. Used for generating filename and reducing tasks names.
 * @param tasks Graph of tasks required to generate diagram.
 * @param dir Optional path to directory for generated file.
 */
fun Any.generatePlanUml(
    tasks: Tasks,
    dir: String = ""
): File = generatePlanUmlFile(
    tasks = tasks,
    path = File(dir).resolve(javaClass.simpleName).absolutePath + "-execute.puml",
    prefixToRemove = javaClass.name
)

/**
 * Generates plantuml file and saves on drive.
 *
 * @param tasks Graph of tasks required to generate diagram.
 * @param path Path to generated file.
 * @param prefixToRemove Optional prefix to remove from each task name.
 */
fun generatePlanUml(
    tasks: Tasks,
    path: String,
    prefixToRemove: String = "",
): File = generatePlanUmlFile(
    tasks = tasks,
    path = path,
    prefixToRemove = prefixToRemove
)

/**
 * Generates plantuml string.
 *
 * @receiver Source object of execution. Used for reducing tasks names.
 * @param tasks Graph of tasks required to generate diagram.
 */
fun Any.generatePlantUml(
    tasks: Tasks
): String = generatePlantUmlString(
    tasks = tasks,
    prefixToRemove = javaClass.name
)

/**
 * Generates plantuml string.
 *
 * @param tasks Graph of tasks required to generate diagram.
 * @param prefixToRemove Optional prefix to remove from each task name.
 */
fun generatePlantUml(
    tasks: Tasks,
    prefixToRemove: String = "",
): String = generatePlantUmlString(
    tasks = tasks,
    prefixToRemove = prefixToRemove
)
