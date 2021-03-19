# CLI

Flank command line interface layer. 

## Basics
From the:
* higher level of view is a bridge between end user and business logic.
* implementation perspective it's just wrapper for the domain function. 

## Package

[`ftl.cli`](https://github.com/Flank/flank/tree/master/test_runner/src/main/kotlin/ftl/cli) and nested.

## Libraries

Flank command line interface is build upon the [picocli](https://picocli.info/).

## Design

The commands design is based on [gcloud firebase](https://cloud.google.com/sdk/gcloud/reference/alpha/firebase), also the
goal is to keep flanks CLI compatible with gcloud.

## Responsibilities

The class which represents the CLI command:

* MUST
    * specify name
    * run dedicated domain function or
    * display help if only groups subcommands
* SHOULD
    * specify information required for help / manual / documentation
* CAN
    * specify subcommands
    * specify options and flags
* CAN'T
    * define logical operations
  
## Class diagram

![cli_class_diagram](http://www.plantuml.com/plantuml/proxy?cache=no&fmt=svg&src=https://raw.githubusercontent.com/Flank/flank/1493-add-cli-class-diagram/docs/hld/cli/cli-class-diagram.puml)