# Architecture

This document describes abstract architecture design which should be able to apply to any use-case in flank scope, starting from user interface and ending on remote API calls. Use it as a reference for explaining common abstract problems around implementation.

# Table of contents

1. [Motivation](#motivation)
1. [Goals](#goals)
1. [Scalability](#scalability)
1. [Layers](#layers)
1. [Presentation](#presentation)
    1. [Responsibilities](#presentation_responsibilities)
    1. [Constrains](#presentconstrains)
    1. [How to scale](#presentation_scale)
    1. [Dependencies](#presentation_dependencies)
1. [Domain](#domain)
    1. [Execution context](#execution_context)
    1. [Top-level function](#top_level_function)
    1. [Low-level function](#low_level_function)
    1. [Responsibilities](#domain_responsibilities)
    1. [Constrains](#domain_constrains)
    1. [How to scale](#domain_scale)
    1. [Dependencies](#domain_dependencies)
    1. [Static](#static_dependencies)
    1. [Dynamic](#dynamic_dependencies)
    1. [Both](#both)
1. [Tool](#tool)
    1. [How to scale](#tool_scale)
    1. [Dependencies](#tool_dependencies)
1. [API](#api)
    1. [How to scale](#api_scale)
    1. [Dependencies](#api_dependencies)
1. [Client](#client)
    1. [How to scale](#client_scale)
    1. [Dependencies](#client_dependencies)
1. [Adapter](#adapter)
    1. [How to scale](#adapter_scale)
    1. [Dependencies](#adapter_dependencies)

# Motivation <a name="motivation"/>

Without well-defined architecture, the application can grow in an uncontrolled way. This typically increases the amount of unwanted redundancy, unneeded calls, and unnecessary logical operations which as result makes code harder to understand, more error-prone, and sometimes even impossible to scale.

# Goals <a name="goals"/>

The architecture should help achieve the following goals:

* Organize implementation into restricted logical layers.
* Divide implementation into small and easy-to-understand parts.
* Identify scalability vectors for each part of the architecture.
* Make implementation easy to navigate through.
* Optimize the amount of code for implementation.
* Make implementation less error-prone.

# Scalability <a name="scalability"/>

The design is specifying two types of scaling:

* `Horizontal` - by adding atomic components that are not related to each other, but must meet common requirements.
* `Vertical` - by expanding one component for new features.

Typically, `horizontal` scaling is preferred when `vertical` scaling become to break the single responsibility principle.

# Layers <a name="layers"/>

The example diagram that is exposing relations between layers:

![architecture_template](http://www.plantuml.com/plantuml/proxy?cache=no&fmt=svg&src=https://raw.githubusercontent.com/Flank/flank/master/docs/hld/architecture-template.puml)

# Presentation <a name="presentation"/>

The front-end layer of the flank application.

* From the higher level of view, the presentation layer is a bridge between end-user and business logic.
* From the implementation perspective it is just adapter for [`domain`](#domain) [`top-level function`](#top_level_function) call.

### Responsibilities <a name="presentation_responsibilities"/>

* Implements user interface and adapt it to [`domain`](#domain) API.
* Converts input from user into [`domain`](#domain) [`top-level functions`](#top_level_function) calls.
* Converts structural output result from the [`domain`](#domain) into the output specific for the `presentation`.
* Passes  [`dynamic`](#dynamic_dependencies) dependencies to [`domain`](#domain) if needed.

### Constrains <a name="presentconstrains"/>

* SHOULD
    * avoid logical operations
* CAN
    * base on third-part framework or library
    * invoke [`domain`](#domain) public functions
* CAN'T
    * access [`API`](#api) layer directly
    * access tools deserved for [`domain`](#domain)

### How to scale <a name="presentation_scale"/>

* [`Horizontal`](#scalability) - by adding different UI implementations
* Along with [`domain`](#domain) [`top-level functions`](#top_level_function).

### Dependencies <a name="presentation_dependencies"/>

* [`Domain`](#domain) layer
* [`Adapter`](#adapter) layer (required meet domain interface)

# Domain <a name="domain"/>

Is the implementation of the application business logic.

Exposes its own API as a one, or many public extension functions, called [`top-level functions`](#top_level_function). Each [`top-level function`](#top_level_function) have its own [`execution context`](#execution_context), can produce a `structured output` during the `execution` and can be composed of one or more [`low-level functions`](#low_level_function).

This layer can be considered as a standalone library, that is providing access to business logic through pure kotlin functions.

### Execution context <a name="execution_context"/>

The context can provide arguments and [`dynamic`](#dynamic_dependencies) functions required by the execution. Its name should reflect the related use case.

### Top-level function <a name="top_level_function"/>

Is a public function placed in root of the domain package. Is responsible to implement domain logic directly or compose it using [`low-level functions`](#low_level_function), [`tools`](#tool) or [`API`](#api). For simplification, consider a simple common type for all [`top-level functions`](#top_level_function).

```kotlin
typealias UseCase<A> = A.() -> Unit
```

Where `A` is a generic type that is representing the [`execution context`](#execution_context).

### Low-level function <a name="low_level_function"/>

The low-level function is useful when it comes to dividing complex [`top-level function`](#top_level_function) into the composition of smaller chunks or the reuse of some logic in many top-level functions. It is crucial in keeping the composition of low-level functions flat. More nesting in-depth can make a code much harder to understand and maintain.

### Responsibilities <a name="domain_responsibilities"/>

* Contains the business logic of the application
* Provide access to the use cases through formalized API

### Constrains <a name="domain_constrains"/>

* MUST
    * define a dedicated [`execution context`](#execution_context) for each [`top-level function`](#top_level_function).
    * keep the [`top-level functions`](#top_level_function) directly inside root package.
    * keep the low-level functions inside nested packages of the root.
* SHOULD
    * access third-party clients through [`API`](#api) layer abstraction.
    * keep the [`execution context`](#execution_context) with related [`top-level function`](#top_level_function) in the same file.
* CAN
    * use [`tools`](#tool) directly.
    * use [`API`](#api) directly.
* CAN'T
    * specify anything else than [`top-level functions`](#top_level_function) & related [`contexts`](#execution_context)
      directly inside root package

### How to scale <a name="domain_scale"/>

* [`Horizontal`](#scalability) - by adding new [`top-level functions`](#top_level_function).
* [`Vertically`](#scalability) - by adding new [`low-level functions`](#low_level_function) for a single [`top-level function`](#top_level_function).

### Dependencies <a name="domain_dependencies"/>

The domain layer shouldn't implement complicated or specialized operations by itself or use third-party libraries directly. Instead of this, it can depend on dedicated internal tools and APIs, that are designed to exactly meet domain requirements. There are 2 types of domain dependencies:

### Static <a name="static_dependencies"/>

The dependencies that are providing its API through static imports.

It's dedicated to the tools that don't need to be mocked for unit testing.

In details the static dependency:

* CAN:
    * Implement algorithms.
    * Generate files.
    * Parse files (only if can generate it also)
    * Format data.
    * Parse data
* CANNOT:
    * Make network calls.
    * Use external applications (shell, SQL server, etc..).
    * Operate on binary files that need to be provided.

### Dynamic <a name="dynamic_dependencies"/>

The dependencies that are provided to the [`domain`](#domain) through the [`execution context`](#execution_context), as a reference.

It's dedicated to the tools that need to be mocked for unit testing.

* CAN:
    * Make network calls.
    * Use external applications (shell, SQL server, etc..).
    * Operate on binary files that need to be provided.
    * Generate data structures.
* CANNOT:
    * Generate files.
    * Implement algorithms.
* SHOULD NOT:
    * Parse formatted data (except simple strings like date).
    * Format data (except simple string formatting).

### Both <a name="both_dependencies"/>

Additionally, both  [`static`](#static_dependencies) and  [`dynamic`](#dynamic_dependencies):

* CAN:
    * Specify data structures.
    * Map data structures.

# Tool <a name="tool"/>

The layer that groups various atomic tools required by the domain. Mainly  [`static`](#static_dependencies) dependencies or not client specific  [`dynamic`](#dynamic_dependencies) dependencies. Typically, tools are specialized to solve one, or a group of related problems like:

* parsing and formatting
* calculating
* mapping

Notice that, tools are solving specialized problems that are meeting [`domain`](#domain) requirements, but should be designed as standalone libraries that do know nothing about the whole domain problem. Instead, just solving well the small highly isolated part. Designing tools as standalone libraries makes the code more decoupled and easier to reuse if needed.

### How to scale <a name="tool_scale"/>

* [`Horizontal`](#scalability) - as a group of libs just by adding more standalone tools if needed.

### Dependencies <a name="tool_dependencies"/>

* third-party library

# API <a name="api"/>

The light-weight layer that is specifying structures and functional interfaces for [`client`](#client) operations. Like [`tool`](#tool) this layer must exactly meet the [`domain`](#domain) requirements and specify public API designed for it. Unlike the [`tool`](#tool), it cannot define any implementation, so it can be scaled horizontally by adding new not unrelated scopes.

### How to scale <a name="api_scale"/>

* [`Horizontal`](#scalability) - by adding more namespaces for structures and functional interfaces.

### Dependencies <a name="api_dependencies"/>

* Only standard libraries

# Client <a name="client"/>

The client-side specific operations not related directly to the domain. Typically, there are two purposes for this layer implementation:

* Is necessary to create a library wrapper for remote protocol, driven on WS, REST, TCP, etc...
* The third-party library is not convenient and requires some adjustments.

### How to scale <a name="client_scale"/>

* Along with third-party API changes.

### Dependencies <a name="client_dependencies"/>

* Network libraries
* third-party client library

# Adapter <a name="adapter"/>

This layer is adapting [`client`](#client) or third-party libraries to structures and interfaces, specified in the [`API`](#api) layer.

### How to scale <a name="adapter_scale"/>

* Along with [`API`](#api) changes.

### Dependencies <a name="adapter_dependencies"/>

one of or many:

* internal [`client`](#client) library
* third-party client library

# Implementation <a name="implementation"/>

For convenience and clarity, the code should be written in a functional programming style.
It's mandatory to avoid the OOP style which almost always makes things much more complicated than should be.


## Public API <a name="implementation_public_api"/>
Any application or library always have a public and internal/private part.
For convenience keep public functions and structures in the root package. Additionally, if the `component`:
*  is providing reach public API with additional structures. - Is mandatory to distinct the public structures and functions from internal implementation which should be kept in nested package(s).
*  is just a simple tool with a compact implementation that not specifies many structures. - Private implementation can be kept in the same file just behind the public signatures or even in one public function.

DO NOT keep many public functions along with internal implementation in the same file or package, because it is messing up the public API, which makes code harder to analyze and navigate.

## Components composition <a name="components-composition"/>

Business logic shouldn't implement complicated tools on its own because it is messing up crucial high-level implementation making it harder to understand.
Instead of this, it should be decomposed into high-level use-case implementation that is operating on tools provided by specialized components.

## Code composition <a name="code-composition"/>

Typically, when huge features are divided into smaller functions and one of those functions is (public) root,
the functions can be composed in two different ways.

### Vertical <a name="code-composition-vertical"/>

The preceding function is calling the following, so the composition of functions is similar to the linked list.

![vertical-composition](http://www.plantuml.com/plantuml/proxy?cache=no&fmt=svg&src=https://raw.githubusercontent.com/Flank/flank/1960_Add_implementation_section_to_architecture_doc/docs/hld/vertical-composition.puml)

Try to **AVOID** this pattern where possible especially in business logic.
In some situations it's can be even worse than one huge monolithic function with comments,
for example when internal functions are not ordered correctly.
Understanding the feature composed in vertical style,
almost always require analyzing the whole chain of functions which typically is not efficient.

### Horizontal <a name="code-composition-horizontal"/>

Root function is controlling independent internal and specialized functions.

![horizontal-composition](http://www.plantuml.com/plantuml/proxy?cache=no&fmt=svg&src=https://raw.githubusercontent.com/Flank/flank/1960_Add_implementation_section_to_architecture_doc/docs/hld/horizontal-composition.puml)

This approach is giving a fast overview of high-level implementation but is hiding the details not important from the high-level perspective.
Comparing to `vertical` composition where the cost of access to internal functions (using reference jumping) in the worst-case scenario is `n`,
the horizontal composition almost always gives `1` on the same layer.

### Layered <a name="code-composition-horizontal-layered"/>

An example of horizontal composition in layered architecture can look as following:

![horizontal-composition-layered](http://www.plantuml.com/plantuml/proxy?cache=no&fmt=svg&src=https://raw.githubusercontent.com/Flank/flank/1960_Add_implementation_section_to_architecture_doc/docs/hld/horizontal-composition-layered.puml)
