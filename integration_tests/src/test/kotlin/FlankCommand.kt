import utils.runCommand
import java.io.File

data class FlankCommand(val flankPath: String, val ymlPath: String, val params: List<String>)

private fun FlankCommand.create() = "java -jar $flankPath ${params.joinToString(separator = " ")} -c=$ymlPath"

fun FlankCommand.run(workingDirectory: String, testSuite: String = "") = create().runCommand(File(workingDirectory), testSuite)
