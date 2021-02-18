# Flank investigation [Not complete]

This document contains investigation of flank architecture, layers and package structure.

## Motivation

Currently, the flank code is not well documented, 
and the only source of true is the implementation. 
The code base has grown over the last year, 
and it's almost impossible to keep in mind whole project.
So to make further work more doable,
it's necessary to identify hidden constraints in code,
expose them in documentation and do refactor.
This document is and entity point for further improvements.

## Layers

### Presentation

### Issues
1. `ftl.Main` command shouldn't be bound with `main` function with constructor
1. Some commands that can run domain code are doing too much.
1. Some of composing commands seem to be in wrong package.

### CLI commands

Flank commands tree.

```
ftl/cli/
├── AuthCommand.kt /
├── FirebaseCommand.kt /
├── auth
│   └── LoginCommand.kt ? !
└── firebase
    ├── CancelCommand.kt ? !
    ├── RefreshCommand.kt ? !
    ├── TestCommand.kt /
    └── test
        ├── AndroidCommand.kt /
        ├── CommandUtil.kt
        ├── CommonRunCommand.kt
        ├── IPBlocksCommand.kt /
        ├── IosCommand.kt /
        ├── NetworkProfilesCommand.kt /
        ├── ProvidedSoftwareCommand.kt /
        ├── android
        │   ├── AndroidDoctorCommand.kt ? !
        │   ├── AndroidRunCommand.kt ? !
        │   ├── AndroidTestEnvironmentCommand.kt ? !
        │   ├── configuration
        │   │   ├── AndroidLocalesCommand.kt / ^
        │   │   ├── AndroidLocalesDescribeCommand.kt ? !
        │   │   └── AndroidLocalesListCommand.kt ? !
        │   ├── models
        │   │   ├── AndroidModelDescribeCommand.kt ? !
        │   │   ├── AndroidModelsCommand.kt / ^
        │   │   └── AndroidModelsListCommand.kt ? !
        │   ├── orientations
        │   │   ├── AndroidOrientationsCommand.kt / ^
        │   │   └── AndroidOrientationsListCommand.kt ? !
        │   └── versions
        │       ├── AndroidVersionsCommand.kt / ^
        │       ├── AndroidVersionsDescribeCommand.kt ? !
        │       └── AndroidVersionsListCommand.kt ? !
        ├── ios
        │   ├── IosDoctorCommand.kt ? !
        │   ├── IosRunCommand.kt ? !
        │   ├── IosTestEnvironmentCommand.kt ? !
        │   ├── configuration
        │   │   ├── IosLocalesCommand.kt / ^
        │   │   ├── IosLocalesDescribeCommand.kt ? !
        │   │   └── IosLocalesListCommand.kt ? !
        │   ├── models
        │   │   ├── IosModelDescribeCommand.kt ? !
        │   │   ├── IosModelsCommand.kt / ^
        │   │   └── IosModelsListCommand.kt ? !
        │   ├── orientations
        │   │   ├── IosOrientationsCommand.kt / ^
        │   │   └── IosOrientationsListCommand.kt ? !
        │   └── versions
        │       ├── IosVersionsCommand.kt / ^
        │       ├── IosVersionsDescribeCommand.kt ? !
        │       └── IosVersionsListCommand.kt ? !
        ├── ipblocks
        │   └── IPBlocksListCommand.kt ?
        ├── networkprofiles
        │   ├── NetworkProfilesDescribeCommand.kt ?
        │   └── NetworkProfilesListCommand.kt ?
        └── providedsoftware
            └── ProvidedSoftwareListCommand.kt ?
```
command that:
* `?` is running domain code
* `/` routes to subcommands
* `!` is doing too much in run function
* `^` should be moved up in package hierarchy

### Domain

#### Issues

1. The core domain api is not easy to identify.
1. The domain layer of flank is not clearly separated of CLI and external APIs.
1. Domain logic is huge and complicated but there is lack of diagram for visualize it.

### Data [ consider better name ]

#### Issues

* External APIs are not hidden behind interfaces
* External API wrappers / adapters are not clearly separated from other layers.

#### External API libs

* `com.google.testing:firebase_apis:test_api`
* `com.google.api-client:google-api-client`
* `com.google.auth:google-auth-library-oauth2-http`
* `com.google.cloud:google-cloud-nio`
* `com.google.cloud:google-cloud-storage`
* `com.google.apis:google-api-services-toolresults`
* `io.sentry:sentry`
* `com.mixpanel:mixpanel-java`
* ... ?
