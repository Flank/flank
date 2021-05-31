# Flank - Corellium - Domain

This module is specifying public API and internal implementation of Flank-Corellium use-cases.

### References

* Module type - [domain](../../docs/architecture.md#domain)
* Dependency type - [static](../../docs/architecture.md#static_dependencies)
* Public API - files inside [flank.corellium.domain](./src/main/kotlin/flank/corellium/domain)
* Internal functions - nested packages inside [flank.corellium.domain](./src/main/kotlin/flank/corellium/domain)

## Design

### Stateful execution

Following specification is suitable for complicated long-running use-cases, when becomes convenient to split execution into smaller atomic chunks of work.

#### Definition:

* `Execution` is process which is creating initial `State`, and is running the set of `steps` on it in specific `Context`.
* `Execution` can pass `Context` to `Step` if needed.
* `Step` is a suspendable operation that is receiving `State` as the only argument.
* `Step` must return received or new `State`.
* `Step` can generate side effects.
* `State` is a structure used for sharing data between preceding and following `steps`.
* `Context` is providing arguments and functions for `step`.

#### Utility:

* [`Transform.kt`](src/main/kotlin/flank/corellium/domain/util/Transform.kt)
