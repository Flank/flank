package ftl.run.status

import com.google.common.annotations.VisibleForTesting
import ftl.args.IArgs
import ftl.args.outputStyle
import ftl.config.FtlConstants
import org.fusesource.jansi.Ansi
import org.fusesource.jansi.AnsiConsole

fun createExecutionStatusPrinter(
    args: IArgs
): (List<ExecutionStatus.Change>) -> Unit = when (args.outputStyle()) {
    OutputStyle.Multi -> MultiLinePrinter()
    OutputStyle.Single -> SingleLinePrinter()
    OutputStyle.Verbose -> VerbosePrinter
}.also {
    AnsiConsole.systemInstall()
}

@VisibleForTesting
internal class SingleLinePrinter : (List<ExecutionStatus.Change>) -> Unit {
    private var previousLineSize = 0
    override fun invoke(changes: List<ExecutionStatus.Change>) {
        val time = changes.firstOrNull()?.time ?: return
        changes.fold(emptyMap<String, Int>()) { acc, (_, _, current: ExecutionStatus) ->
            acc + Pair(
                first = current.state,
                second = acc.getOrDefault(current.state, 0) + 1
            )
        }.toList().joinToString(", ") { (status, count) ->
            "$status:$count"
        }.let { statusCounts ->
            print("\r" + (0..previousLineSize).joinToString("") { " " })
            previousLineSize = statusCounts.length
            print("\r${FtlConstants.indent}$time Test executions status: $statusCounts")
        }
    }
}

@VisibleForTesting
internal class MultiLinePrinter : (List<ExecutionStatus.Change>) -> Unit {
    private val output = LinkedHashMap<String, ExecutionStatus.View>()
    override fun invoke(changes: List<ExecutionStatus.Change>) {
        repeat(output.size) {
            print(Ansi.ansi().cursorUpLine().eraseLine())
        }
        output += changes.map { change ->
            listExecutionStatusView(change).takeLast(1)
        }.flatten().map { view ->
            view.id to view
        }
        output.values.joinToString("\n").let(::println)
    }
}

@VisibleForTesting
internal object VerbosePrinter : (List<ExecutionStatus.Change>) -> Unit {
    override fun invoke(changes: List<ExecutionStatus.Change>) {
        changes.map(listExecutionStatusView).flatten().forEach(::println)
    }
}

private val listExecutionStatusView: (ExecutionStatus.Change) -> List<ExecutionStatus.View>
    get() = { (name, previous, current, time) ->
        emptyList<ExecutionStatus.View>()
            .plus(
                current.progress.minus(previous.progress).map { message ->
                    ExecutionStatus.View(time, name, message)
                }
            )
            .let { list ->
                if (current.error != previous.error && current.error != null)
                    list + ExecutionStatus.View(time, name, "Error: ${current.error}")
                else list
            }
            .let { list ->
                if (current.state != previous.state)
                    list + ExecutionStatus.View(time, name, current.state)
                else list
            }
    }
