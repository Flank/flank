# Flank refactor proposal [Not complete]

## Motivation

The new opportunities like desktop UI or Corellium integration,
requires more flexibility from the Flank.
The flexibility in this case means replaceable presentation and external APIs,
but also well organized and documented code base, easy to scale and navigate through.  

## References

* [investigation](./investigation.md)


## Architecture

#### Layers associations

```puml
presentation -> domain
presentation -> utils
domain -> utils
domain -> interfaces
adapters -> interfaces
adapters -> external API
```

#### Consider interfaces as boundaries

Interfaces in this case means communication bridge between flank domain code and external APIs.
So those are boundaries from the domain perspective, 
but from technical perspective those will be interfaces, type aliases, and structures. 

#### Package structure

* ftl
    * cli
    * domain
    * interfaces
    * data ?
    * utils
   

### CLI 
Wouldn't change a lot in comparison to current state.
Should be thin as possible and aware only about domain layer API (top-level public functions and structures) and some simple utils.

#### Requirements

* Aware about
    * CLI library
    * domain API - top-level public functions
    * adapters - for mapping results from the domain to console logs.
* Not aware about
    * data
    * low-level domain functions
    * helpers / libs
* Consider awareness about 
    * common utils (or have dedicated one).

### Domain

#### Requirements

* New package for grouping domain code
* Domain package directly can contain only:
    * top-level public functions that represents use-cases(?).
    * packages that contains low-level domain logic functions.
* Top-level domain functions
    * are aware about
        * own private functions
        * low-level domain functions from nested domain packages.
        * flank utils
        * flank internal helpers
    * are not aware about 
        * other top-level domain function
        * CLI
        * external API libs
    * can produce flow of results
    * are suspendable

#### Top-level function

Responsible to run domain logic directly or compose it using
low-level domain functions, utils or API interfaces.
For simplification, we can consider a simple common interface
for all top-level functions.
```kotlin
typealias UseCase<A, R> = suspend (T) -> Flow<R>
```

### Data

#### Requirements

* New package for grouping wrappers and adapters for external APIs
* TODO