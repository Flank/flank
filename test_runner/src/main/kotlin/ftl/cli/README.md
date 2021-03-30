# CLI

Flank command line interface layer.

## Libraries

Flank command line interface is build upon the [picocli](https://picocli.info/).

## Design

* The commands design is based on [gcloud firebase](https://cloud.google.com/sdk/gcloud/reference/alpha/firebase),
* The goal is to have implemented equivalent for each gcloud command in firebase scope, so Flank will keep CLI parity with gcloud.

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

## Diagram

![cli_class_diagram](http://www.plantuml.com/plantuml/proxy?cache=no&fmt=svg&src=https://raw.githubusercontent.com/Flank/flank/master/docs/hld/presentation/cli/cli-class-diagram.puml)

### Notes

* Click on the class name will navigate to `kt` source file on github.
* Click on the `local link` is compatible with intellij PlantUML plugin.
* Click on `github adoc` will navigate to command documentation on github.
* Click on `gcloud equivalent` will navigate to documentation page of gcloud command equivalent.
* Classes that are calling domain functions by overridden `run` are marked with `#lightblue` background.

## Shortcuts

Flank also specifies shortcuts to some commands. Those relations are not shown on the diagram, because are producing a
more complicated graph. Down below, you can read the list of commands that are missing on the diagram.

| Class | Command |
|:-------:|:-------:| 
| RefreshCommand | `refresh`|
| CancelCommand | `cancel`|
| ProvidedSoftwareCommand | `provided-software`|
| NetworkProfilesCommand | `network-profiles`|
| IPBlocksCommand | `ip-blocks`|
| IosCommand | `ios`|
| AndroidCommand | `android`| 

### Notes

* For preceding commands you can omit the `firebase test` prefix, just type the command name as first argument
  like `$ flank.jar refresh`.
* Shortcuts are working also with subcommands chaining like `$ flank.jar ip-blocks list`
