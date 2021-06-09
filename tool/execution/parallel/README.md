# Parallel Execution

Library for task parallelization and asynchronous execution.

### References

* Module type - [tool](../../docs/architecture.md#tool)
* Dependency type - [static](../../docs/architecture.md#static_dependencies)
* Public API
    * Core - [Parallel.kt](./src/main/kotlin/flank/exection/parallel/Parallel.kt)
    * Task factories DSL - [Factory.kt](./src/main/kotlin/flank/exection/parallel/Factory.kt)
    * Extensions - [Ext.kt](./src/main/kotlin/flank/exection/parallel/Ext.kt)

# Design

1. Imagine a complicated long-running `execution` that needs to perform several operations (`tasks`) in correct order to collect a required `data` and produce `side effects`.
2. Any `execution` like that, can be modeled as the set of unrelated data `types` and suspendable functions (`tasks`).

## Execution

* will run tasks in optimized order creating synchronization points where needed and running all other stuff in parallel.
* is collecting result from each task, and accumulating it to state.
* is returning the flow of realtime state changes.
* can detach and fail if set of `tasks` are creating broken graph with `missing dependencies`.
* Each task in execution scope must have unique return type. This is crucial for producing correct graph.

The following diagram is showing parallel execution algorithm in details:

![parallel-execution](http://www.plantuml.com/plantuml/proxy?cache=no&fmt=svg&src=https://raw.githubusercontent.com/Flank/flank/2001_Implement_tool_for_parallel_execution/docs/hld/parallel-execution.puml)

## Type

* is atomic and unique in the execution scope.
* is describing the data type for value that can be:
    * passed to execution as arguments for initial state. 
    * passed to task from state as argument.
    * returned from task.
    * returned from execution.

## Task

* `arguments` for task are coming from `initial state` (`arguments`) or results from other `tasks`.
* can be grouped into set to create the `execution` graph.
* graph can have broken paths, when any task have `missing dependencies`.

### Signature

* is a set of `argument` `types` related with one `return` `type` just like in normal functions.

### Function (ExecuteTask)

* is getting access to arguments through reduced copy of state.
* is returning object described by the return type.

### Missing dependencies

* missing dependencies can be detected before execution from the set of `tasks` and `initial state`.

## State

* is a map of types with associated values that are coming from initial arguments or task results.
* is storing the result from each executed task.
* is used for providing arguments for tasks.
* is emitted in flow after each change. 

## API

![parallel-execution](http://www.plantuml.com/plantuml/proxy?cache=no&fmt=svg&src=https://raw.githubusercontent.com/Flank/flank/2001_Implement_tool_for_parallel_execution/docs/hld/parallel-execution-api-functions.puml)
![parallel-execution](http://www.plantuml.com/plantuml/proxy?cache=no&fmt=svg&src=https://raw.githubusercontent.com/Flank/flank/2001_Implement_tool_for_parallel_execution/docs/hld/parallel-execution-api-structures.puml)
