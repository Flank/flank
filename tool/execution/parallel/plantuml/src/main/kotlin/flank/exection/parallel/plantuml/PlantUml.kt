package flank.exection.parallel.plantuml

import flank.exection.parallel.Tasks
import flank.exection.parallel.plantuml.internal.generatePlanUmlFile
import flank.exection.parallel.plantuml.internal.generatePlantUmlString
import java.io.File

/**
 * Generates plant uml file and save on drive.
 *
 * @receiver Source object of execution. Used for generating filename and reducing tasks names.
 * @param tasks Tasks relations graph required to generate diagram.
 * @param dir optional path to directory for generated file.
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
 * Generates plant uml file and save on drive.
 *
 * @param tasks Tasks relations graph required to generate diagram.
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
 * Generates plant uml string.
 *
 * @receiver Source object of execution. Used for reducing tasks names.
 * @param tasks Tasks relations graph required to generate diagram.
 */
fun Any.generatePlantUml(
    tasks: Tasks
): String = generatePlantUmlString(
    tasks = tasks,
    prefixToRemove = javaClass.name
)

/**
 * Generates plant uml string.
 *
 * @param tasks Tasks relations graph required to generate diagram.
 * @param prefixToRemove Optional prefix to remove from each task name.
 */
fun generatePlantUml(
    tasks: Tasks,
    prefixToRemove: String = "",
): String = generatePlantUmlString(
    tasks = tasks,
    prefixToRemove = prefixToRemove
)
