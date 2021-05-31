## Corellium Client

The kotlin implementation of Corellium API client based on official [JS implementation](https://github.com/corellium/corellium-api).

Be aware, this library is designed for the Flank-Corellium integration, so the features not required by Flank but available in official client, may not be provided here, also data structures may not contain all properties.

### References

* Official Corellium JS client - [corellium-api](https://github.com/corellium/corellium-api)
* Official JS example - [agent-simple.js](https://github.com/corellium/corellium-api/blob/master/examples/agent-simple.js)
* HTTP client - [KTOR](https://ktor.io/)

## API

Access to the Corellium API is provided through the connection contexts, that are represented by structures. Connection contexts along with API functions are creating hierarchical relations visible on following diagram:

![corellium-client](http://www.plantuml.com/plantuml/proxy?cache=no&fmt=svg&src=https://raw.githubusercontent.com/Flank/flank/1951_Improve_Flank-Corellium_documentation/docs/corellium/client-api-class.puml)

### Corellium

The root connection context that providing core API functions.

To gain access to the root context, call:

```kotlin
val corellium: Corellium = connectCorellium(
    api = "your.company.enterprise.corellium.com", // without protocol (https)
    username = "your account user name",
    password = "your account password",
)
```

### Agent

The connection context for operations on virtual devices [`instance`](#instance)

To gain access to the agent context, call:

```kotlin
val instanceId = "..."
val instance: Instance = corellium.getInstanceInfo(instanceId)
val agent: Agent = corellium.connectAgent(instance.agent!!.info)
```

To obtain `instanceId`, create new instance:

```kotlin
val instanceId: String = corellium.createNewInstance(
    Instance(
        project = "11111-aaaaa-bbbbb-33333",
        name = "test-iphone",
        flavor = "iphone7",
        os = "13.0",
        bootOptions = BootOptions(
            screen = screen
        )
    )
)
```

Or find the existing one:

```kotlin
val projectId = "1111-2222-3333-aaa-bbbbbb-cccc"
val instanceName = "Some instance 1"
val instance: Instance = corellium.getProjectInstancesList(projectId).find { instance -> instance.name == instanceName }!!
val instanceId: String = instance.id 
```

### Console

The context of connection to the shell of specific instance.

```kotlin
val console: Console = corellium.connectConsole(instanceId)
```

### Instance

The virtual instance of ARM device.

## Additional Info

* Creating new instance usually requires USBFlux restart
