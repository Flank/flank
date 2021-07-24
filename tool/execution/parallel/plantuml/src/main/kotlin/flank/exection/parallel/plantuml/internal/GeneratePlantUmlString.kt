package flank.exection.parallel.plantuml.internal

import flank.exection.parallel.Parallel
import flank.exection.parallel.Tasks
import java.awt.Color

// =================== Internal API ===================

internal fun generatePlantUmlString(
    tasks: Tasks,
    prefixToRemove: String = "",
): String {
    val graph: Graph = tasks.associate { task -> task.signature.type to task.signature.args }
    val depth: Map<Node, Int> = graph.calculateDepth()
    val maxDepth: Int = depth.values.maxOrNull() ?: 0
    val colors: Colors = depth.mapValues { (_, value) -> calculateColor(maxDepth, value) }
    val name = fun Any.() = javaClass.name.removePrefix("$prefixToRemove$").replace('$', '.').let { "[$it]" }

    return """
@startuml

skinparam componentStyle rectangle

note as N #ffffff
* Brighter tasks are required by the darker tasks.
* The brightness means how fast the task will start.
* White tasks are starting first.
end note

${colors.printColors(name)}

${graph.printRelations(name)}

@enduml
    """.trimIndent()
}

// =================== Private implementation ===================

private typealias Graph = Map<Node, Set<Node>>
private typealias Node = Parallel.Type<*>
private typealias Colors = Map<Node, String>

private fun calculateColor(maxDepth: Int, value: Int): String {
    val c = (COLOR_MAX / maxDepth) * (maxDepth - value) + COLOR_OFFSET
    return Integer.toHexString(Color(c, c, c).rgb).drop(2) // drop alpha
}

private const val COLOR_MAX = 200
private const val COLOR_OFFSET = 55

private fun <T> Map<T, Set<T>>.calculateDepth(): Map<T, Int> {
    var jump = 0
    val state = (values.flatten() - keys).toMutableList()
    val remaining: MutableMap<T, Set<T>> = toMutableMap()
    val depth = mutableMapOf<T, Int>()

    while (depth.size < size) {
        val current = remaining
            .filterValues { state.containsAll(it) }.keys
            .onEach { depth[it] = jump }
        state += current
        remaining -= current
        jump++
    }

    return depth
}

private fun Colors.printColors(
    name: Node.() -> String
) = toList().joinToString("\n") { (node, value) ->
    "${node.name()} #$value"
}

private fun Graph.printRelations(
    name: Node.() -> String
) = filterValues { dependencies ->
    dependencies.isNotEmpty()
}.toList().joinToString("\n") { (node, dependencies) ->
    dependencies.joinToString("\n") { dep ->
        node.name() + " --> " + dep.name()
    }
}
