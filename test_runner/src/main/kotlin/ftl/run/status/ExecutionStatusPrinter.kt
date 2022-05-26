package ftl.run.status

import com.google.common.annotations.VisibleForTesting
import flank.common.OutputLogLevel
import flank.common.log
import flank.common.logLn
import ftl.args.IArgs
import ftl.config.FtlConstants
import org.fusesource.jansi.Ansi
import org.fusesource.jansi.AnsiConsole

fun createExecutionStatusPrinter(
    args: IArgs
): (List<ExecutionStatus.Change>) -> Unit = when (args.outputStyle) {
    OutputStyle.Multi -> MultiLinePrinter()
    OutputStyle.Single -> SingleLinePrinter()
    OutputStyle.Verbose -> VerbosePrinter
    OutputStyle.Compact -> VerbosePrinter
}

@VisibleForTesting
internal class SingleLinePrinter : (List<ExecutionStatus.Change>) -> Unit {
    private var previousLineSize = 0
    private val cache = mutableMapOf<String, ExecutionStatus.Change>()
    override fun invoke(changes: List<ExecutionStatus.Change>) {
        cache += changes.associateBy(ExecutionStatus.Change::name)
        val time = changes.firstOrNull()?.time ?: return
        cache.values.fold(emptyMap<String, Int>()) { acc, (_, _, current: ExecutionStatus) ->
            acc + Pair(
                first = current.state,
                second = acc.getOrDefault(current.state, 0) + 1
            )
        }.toList().joinToString(", ") { (status, count) ->
            "$status:$count"
        }.let { statusCounts ->
            print("\r" + (0..previousLineSize).joinToString("") { " " })
            val output = "${FtlConstants.indent}$time Test executions status: $statusCounts"
            previousLineSize = output.length
            log("\r$output")
        }
    }
}

@VisibleForTesting
internal class MultiLinePrinter(
    private val ansi: () -> Ansi = { Ansi.ansi() }
) : (List<ExecutionStatus.Change>) -> Unit {
    init {
        AnsiConsole.systemInstall()
    }

    private val output = LinkedHashMap<String, ExecutionStatus.View>()
    override fun invoke(changes: List<ExecutionStatus.Change>) {
        repeat(output.size) {
            log(ansi().cursorUpLine().eraseLine().toString())
        }
        output += changes.map { change ->
            change.views.takeLast(1)
        }.flatten().map { view ->
            view.id to view
        }
        output.values.joinToString("\n").let(::println)
    }
}

@VisibleForTesting
internal object VerbosePrinter : (List<ExecutionStatus.Change>) -> Unit {
    override fun invoke(changes: List<ExecutionStatus.Change>) {
        changes.map(ExecutionStatus.Change::views).flatten().forEach { logLn(it, OutputLogLevel.DETAILED) }
    }
}

private val ExecutionStatus.Change.views
    get() = emptyList<ExecutionStatus.View>().plus(
        current.progress.minus(previous.progress).map { message ->
            ExecutionStatus.View(time, name, message)
        }
    ).let { list ->
        // the null error value from the FTL API is stored as an empty string in Kotlin
        if (current.error.isNullOrEmpty() || current.error == previous.error) list
        else list + ExecutionStatus.View(time, name, "Error: ${current.error}")
    }.let { list ->
        if (current.state == previous.state) list
        else list + ExecutionStatus.View(time, name, current.state)
    }
