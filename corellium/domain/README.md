# Flank - Corellium - Domain

This module is specifying public API and internal implementation of Flank-Corellium use-cases.

### References

* Module type - [domain](../../docs/architecture.md#domain)
* Dependency type - [static](../../docs/architecture.md#static_dependencies)
* Public API - files inside [flank.corellium.domain](./src/main/kotlin/flank/corellium/domain)
* Internal functions - nested packages inside [flank.corellium.domain](./src/main/kotlin/flank/corellium/domain)

## Execution

Execution can be represented as a graph of tasks relations without cycles.

#### Version from master branch:

Core execution.

![TestAndroid.execute](http://www.plantuml.com/plantuml/proxy?cache=no&fmt=svg&src=https://raw.githubusercontent.com/Flank/flank/master/corellium/domain/TestAndroid-execute.puml)

Device sub-execution triggered for each shard or rerun by the `Device.Tests` task.

![TestAndroid.Device.execute](http://www.plantuml.com/plantuml/proxy?cache=no&fmt=svg&src=https://raw.githubusercontent.com/Flank/flank/master/corellium/domain/TestAndroid_Device-execute.puml)

### New version draft:

Core execution.

![TestAndroid.execute](http://www.plantuml.com/plantuml/proxy?cache=no&fmt=svg&src=https://raw.githubusercontent.com/Flank/flank/2083_test_dispatch_flow/corellium/domain/TestAndroid-execute.puml)

Device sub-execution triggered for each shard or rerun by the `Device.Tests` task.

![TestAndroid.Device.execute](http://www.plantuml.com/plantuml/proxy?cache=no&fmt=svg&src=https://raw.githubusercontent.com/Flank/flank/2083_test_dispatch_flow/corellium/domain/TestAndroid_Device-execute.puml)
