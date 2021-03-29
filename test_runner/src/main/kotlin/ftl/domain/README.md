# Domain

Owns the business logic of the application.

## Design

The domain layer is exposing its own API as a collection of public suspendable extension functions,
which could be called `top-level functions`.
Each `top-level function` have its own `execution context`, 
can produce `structured output` during the `execution`
and can be composed of one or many low-level functions.
This layer could be considered as a standalone library, 
that is providing access to business logic through pure kotlin functions. 


### Top-level function

Is public and accessible from the root of the domain package.
Is responsible to run domain logic directly or compose it using low-level domain functions, utils, or data interfaces.
For simplification, consider a simple common interface for all top-level functions.

```kotlin
typealias UseCase<A> = suspend A.() -> Unit
```

Where `A` is a generic type that is representing the `execution context`.

### Execution context

The context can provide arguments for the execution and its name should reflect the related use case.

### Low-level function

The low-level function is useful when it comes to dividing complex top-level 
functions into the composition of smaller chunks or the reuse of some logic in many top-level functions.
It is crucial in keeping the composition of low-level functions flat. 
More nesting in depth, can make a code much harder to understand and maintain.

## Responsibility

* Contains the business logic of the application
* Provide access to the use cases through formalized API

## Requirements

* MUST
    * define a dedicated `execution context` for each top-level function.
    * keep the top-level functions directly inside `ftl.domain` package.
    * keep the low-level functions inside nested packages in `ftl.domain`.
* SHOULD
    * access external APIs through data layer abstraction.
    * keep the `execution context` with related `top-level function` in the same file.
* CAN
    * use utils directly.
    * use crucial third-part utils directly (for example `gson` or `jackson`).
    * specify nested packages inside `ftl.domain` for internal dependencies.
