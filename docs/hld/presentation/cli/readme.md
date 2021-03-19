# CLI

Flank command line interface layer.

## Basics

From the:

* higher level of view CLI layer is a bridge between end user and business logic.
* implementation perspective it is just adapter for domain function call.

## Package

[`ftl.cli`](https://github.com/Flank/flank/tree/master/test_runner/src/main/kotlin/ftl/cli) and nested.

## Libraries

Flank command line interface is build upon the [picocli](https://picocli.info/).

## Design

The commands design is based on [gcloud firebase](https://cloud.google.com/sdk/gcloud/reference/alpha/firebase), also
the goal is to keep flanks CLI compatible with gcloud.

## Responsibilities

The class which represents the CLI command:

* MUST
    * specify command name
    * override run method for
        * running dedicated domain function or
        * displaying help if only groups subcommands
* SHOULD
    * specify information required for help / manual / documentation
* CAN
    * specify subcommands
    * specify options and flags
* CAN'T
    * define logical operations

## Class diagram

![cli_class_diagram](http://www.plantuml.com/plantuml/proxy?cache=no&fmt=svg&src=https://raw.githubusercontent.com/Flank/flank/1493-add-cli-class-diagram/docs/hld/presentation/cli/cli-class-diagram.puml)

## Shortcuts

Flank also specifies shortcuts to some commands. 
Those relations are not shown on the diagram, because are producing a more complicated graph. 
Down below, you can read the list of commands that are missing on the diagram.

| Class | Command |
|:-------:|:-------:| 
| RefreshCommand | `refresh`|
| CancelCommand | `cancel`|
| ProvidedSoftwareCommand | `provided-software`|
| NetworkProfilesCommand | `network-profiles`|
| IPBlocksCommand | `ip-blocks`|
| IosCommand | `ios`|
| AndroidCommand | `android`| 

Be aware that
* for preceding commands you can omit the `firebase test` prefix, just call the command name.
* shortcuts are working also with subcommands chaining.
