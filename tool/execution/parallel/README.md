# Parallel Execution

Library for task parallelization and asynchronous execution.

### References

* Module type - [tool](../../docs/architecture.md#tool)
* Dependency type - [static](../../docs/architecture.md#static_dependencies)
* Public API
    * Core - [Parallel.kt](./src/main/kotlin/flank/execution/parallel/Parallel.kt)
    * Execution - [Execute.kt](./src/main/kotlin/flank/execution/parallel/Execute.kt)
    * Task factories DSL - [Create.kt](./src/main/kotlin/flank/execution/parallel/Create.kt)
    * Graph validation - [Create.kt](./src/main/kotlin/flank/execution/parallel/Validate.kt)
    * Graph reduction - [Reduce.kt](./src/main/kotlin/flank/execution/parallel/Reduce.kt)
* Example usage - [Example.kt](./src/test/kotlin/flank/execution/parallel/Example.kt)

# Design

Imagine a complicated long-running `execution` that needs to perform several operations (`tasks`) in correct order to
collect required `data` and produce `side effects`. Any `execution` like that, can be modeled as a set of unrelated
data `types` and suspendable functions (`tasks`).

#### Example execution graph

![example-graph](http://www.plantuml.com/plantuml/proxy?cache=no&fmt=svg&src=https://raw.githubusercontent.com/Flank/flank/master/docs/hld/parallel-example-graph.puml)

## Type

* is unique in the execution scope.
* is describing the data type for value that can be:
    * passed to execution as arguments for initial state.
    * passed to task from state as argument.
    * returned from task.
    * returned from execution.

#### Example kotlin code

```kotlin
object Args : Parallel.Type<List<Any>>
object Foo : Parallel.Type<String>
object Bar : Parallel.Type<Int>
object Baz : Parallel.Type<Boolean>
object Logger : Parallel.Type<Output>
```

## Task

* is representing a single atomic operation in the execution process.
* is a relation of signature and task function.
* `arguments` for task are coming from `initial state` (`arguments`) or results from other `tasks`.
* can be uniquely identified using the return type of its signature.

### Signature

* is a set of argument `types` related with one return `type` just like in a normal functions.

### Function (ExecuteTask)

* is getting access to arguments through reduced copy of state.
* is returning object described by the return type.

### Missing dependencies

* argument types that are not specified as return types by any task in the graph or provided in the initial state.
* missing dependencies can be detected before execution from the set of `tasks` and `initial state`.

### Graph

* can be represented as tree structure without cycles, where each type can reference others.
* can be created from a set of tasks.
* can have broken paths, when any task have `missing dependencies`.

![parallel-execution](http://www.plantuml.com/plantuml/proxy?cache=no&fmt=svg&src=https://raw.githubusercontent.com/Flank/flank/master/docs/hld/task-graph.puml)

#### Example kotlin code

```kotlin

// Context with accessors to state values. 
class Context : Parallel.Context() {
    // Delegates specified with '!' are used for validating initial state.
    // Are provided by default to each task.
    val args by !Args
    val out by !Logger

    // Dynamic properties for providing results from tasks as arguments for other tasks.
    val foo by -Foo
    val bar by -Bar
    val baz by -Baz
}

// Helper for creating task functions with context. 
val context = Parallel.Function(::Context)

// Set of tasks to execute.
val execute = setOf(
    // Basic task without arguments and access to the context.
    Baz using { false },
    // Task that uses context to access state values, initial properties doesn't need to be specified in arguments. 
    Bar using context { args.size },
    // Task that specifies required arguments and generates side effect by emitting value to output
    Foo from setOf(Bar, Baz) using context { "$bar, $baz".apply(out) }
)
```

## State

* is a map of types with associated values that are coming from initial arguments or task results.
* is storing the result from each executed task.
* is used for providing arguments and dependencies for tasks.
* is emitted in flow after each change.

#### Example kotlin code

```kotlin
// Preparing initial state
val initial = mapOf(
    Args to listOf(),
    Logger to { any: Any -> println(any) },
)
```

## Execution

* will run tasks in optimized order creating synchronization points where needed and running all other stuff in
  parallel.
* is collecting result from each task, and accumulating it to state.
* is returning the flow of realtime state changes.
* can detach and fail if set of `tasks` are creating broken graph with `missing dependencies`.
* Each task in execution scope must have unique return type. This is crucial for producing correct graph.

#### Example kotlin code

```kotlin

// For execution without arguments 
val flow1: Flow<ParallelState> = execute()

// For execution with arguments (initial state)
val flow2: Flow<ParallelState> = execute(initial)

// For execution reduced to selected types with dependencies. 
val flow3: Flow<ParallelState> = execute(Bar, Baz)(initial)
```

### Process

The following diagram is showing parallel execution algorithm in details:

![parallel-execution](http://www.plantuml.com/plantuml/proxy?cache=no&fmt=svg&src=https://raw.githubusercontent.com/Flank/flank/master/docs/hld/parallel-execution.puml)

# API

The following graphs are showing constrains between API elements described previously.

### Functions

![parallel-execution](http://www.plantuml.com/plantuml/proxy?cache=no&fmt=svg&src=https://raw.githubusercontent.com/Flank/flank/master/docs/hld/parallel-execution-api-functions.puml)

### Types

![parallel-execution](http://www.plantuml.com/plantuml/proxy?cache=no&fmt=svg&src=https://raw.githubusercontent.com/Flank/flank/master/docs/hld/parallel-execution-api-structures.puml)
