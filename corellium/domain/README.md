# Flank-Corellium Domain Layer

This module is specifying public API and internal implementation of flank-corellium use-cases.

## Public API

Functions and structures which are specified directly inside [flank.corellium.domain](./src/main/kotlin/flank/corellium/domain) package.

## Internal implementation

Everything inside nested packages of [flank.corellium.domain](./src/main/kotlin/flank/corellium/domain).

## Design

### Stateful execution

Following specification is suitable for complicated long-running use-cases,
when becomes convenient to split execution into smaller atomic chunks of work.

#### Definition:

* `Execution` is process which is creating initial `State`, and is running the set of `steps` on it in specific `Context`.
* `Execution` can pass `Context` to `Step` if needed.
* `Step` is a suspendable operation that is receiving `State` as the only argument.
* `Step` must return received or new `State`.
* `Step` can generate side effects.
* `State` is a structure used for sharing data between preceding and following `steps`.
* `Context` is providing arguments and functions for `step`.

#### Utility:

`Transform.kt`
* [local link](src/main/kotlin/flank/corellium/domain/util/Transform.kt)
* [github link](https://github.com/Flank/flank/blob/master/corellium/domain/src/main/kotlin/flank/corellium/domain/util/Transform.kt)
