# Presentation

The front-end layer of the flank application.

## Basics

From the:

* higher level of view the presentation layer is a bridge between end user and business logic.
* implementation perspective it is just adapter for domain function call.

## Entry point

The [main](https://github.com/Flank/flank/blob/master/test_runner/src/main/kotlin/ftl/Main.kt) function

## Responsibilities

* Converting input into public domain functions calls.
* Mapping structural results from the domain output into the output specific for the presentation.

## Requirements

* SHOULD
    * be thin as possible
    * avoid logical operations
* CAN
    * base on third-part framework or library
    * invoke public domain functions
* CAN'T
    * access data layer directly
