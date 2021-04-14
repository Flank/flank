## About

Only for POC integration purposes. It's based on [corellium-api](https://github.com/corellium/corellium-api). It has
only essential features implemented to be able to run a simple set of IOS tests that coincide
to [example](https://github.com/corellium/corellium-api/blob/master/examples/agent-simple.js)

## **IT IS DEFINITELY NOT PRODUCTION READY!**

There are lots of features missing (compared to nodeJS corellium [client](https://github.com/corellium/corellium-api)).

## Installing

JDK 8+ is required (tested on java 8 & 15). [KTOR](https://ktor.io/) is used as HTTP client.

To build just run (from the root of the project)

```
./gradlew build -p corellium/client
```

## API

### class Corellium

-------

#### new instance

Creates new corellium client instance. Options:

* `api` -> your corellium's http adrress, example: `yourcompany.enterprise.corellium.com` (without `https`)
* `username`, `password` -> credentials to log in and acquire token
* `tokenFallback` (optional) -> pass if you don't want to log in
* `logging` (optional) -> logging level for http requests, default: `None`. More
  info [LoggingLevel](#sealed-class-logginglevel)

```kotlin
val client = Corellium(
    api = String,
    username = String,
    password = String,
    tokenFallback = String,
    logging = LoggingLevel
)
```

#### logIn(): String (token)

Logs in with given credentials. Acquired `token` is persisted inside the client. It is also returned from the function
if you want to reuse it

```kotlin
val token: String = client.logIn()
```

#### getProjectIdList(): List<String>

Returns a list of project IDs associated with the given credentials

```kotlin
val ids: List<String> = client.getProjectIdList()
```

#### getAllProjects(): List<Project>

Returns a list of [Project](#data-class-project) associated with the given account

```kotlin
val projects: List<Project> = client.getAllProjects()
val testProjectId = projects.first { it.name == "test-project" }.id
```

#### getProjectInstancesList(projectId: String): List<Instance>

Returns a list of [Instance](#data-class-instance) created for the project with the given id

```kotlin
val projectId = "1111-2222-3333-aaa-bbbbbb-cccc"
val instances: List<Instance> = client.getProjectInstancesList(projectId)
```

#### createNewInstance(newInstance: Instance): String (instance id)

Creates a new [Instance](#data-class-instance), returns newly created instance id.

NOTE: instance created with this method is not ready to use (state ==  `creating`).
Use [waitUntilInstanceIsReady](#waituntilinstanceisreadyinstanceid-string) to ensure VM has `on` state

The following options are required (all `Instance` fields are described [here](#data-class-instance))

* `project` -> id of the project your instance is going to be created in
* `name` -> instance's name
* `flavor` -> flavor of the instance. Currently supported devices are
  listed [here](https://github.com/corellium/corellium-api#async-createinstanceoptions)
* `os` -> software version

```kotlin
val newInstanceId: String = client.createNewInstance(
    Instance(
        project = "11111-aaaaa-bbbbb-33333",
        name = "test-iphone",
        flavor = "iphone7",
        os = "13.0"
    )
)
```

#### waitUntilInstanceIsReady(instanceId: String)

Waits until instance with `id` == `instanceId` is ready to use (`state` is `on`). It's `suspend` function so the thread
is not blocked.

```kotlin
val instanceId = "11111-aaaaa-bbbbb-33333"
client.waitUntilInstanceIsReady(instanceId)
```

#### getInstanceInfo(instanceId: String): Instance

Returns [Instance](#data-class-instance) for given `instanceId`

```kotlin
val instanceId = "11111-aaaaa-bbbbb-33333"
val instance: Instance = client.getInstanceInfo(instanceId)
```

#### deleteInstance(instanceId: String)

Deletes instance by id

```kotlin
val instanceId = "11111-aaaaa-bbbbb-33333"
client.deleteInstance(instanceId)
```

#### createAgent(agentInfo: String): Agent

Creates [Agent](#class-agent) for the given instance

**NOTE**: Creating an agent can be flaky sometimes and may throw 5xx errors. There is the retry mechanism implemented
but sometimes it's not enough.

```kotlin
val instanceId = "11111-aaaaa-bbbbb-33333"
val instance = client.getInstanceInfo(instanceId)
val agent: Agent = client.createAgent(instance.agent.info)
```

#### getVPNConfig(projectId: String, type: VPN)

Downloads VPN config file. Currently, filename is hardcoded:

* `config.ovpn` -> for `VPN.OVPN`
* `tblk.zip` -> for `VPN.TBLK`

```kotlin
client.getVPNConfig(
    projectId = "11111-aaaaa-bbbbb-33333",
    type = VPN.TBLK,
    id = "asdasd-adaag-324234-dfsdf" // optional, if not passed, UUID.randomUUID() is used to generate
)
```

### class Agent

-----

This is a wrapper class
for [ClientWebSocketSession](https://api.ktor.io/1.5.1/io.ktor.client.features.websocket/-client-web-socket-session/index.html)
with additional logic.

#### new instance

Should be created only with factory method [Corellium#createAgent](#createagentagentinfo-string-agent).

#### uploadFile(path: String, bytes: ByteArray)

Uploads file (`ByteArray`) to the given `path`, on the device agent is connected to.

```kotlin
val path = "/var/usr/temp-file"
val bytes = File("path/to/file").readBytes()

agent.uploadFile(path, bytes)
```

#### close()

Closes agent's connection.

```kotlin
agent.close()
```

#### waitForAgentReady()

Suspends coroutine until agent connection is established and ready to use

```kotlin
agent.waitForAgentReady()
```

### data class Project

----
**NOTE**: Does not contain all fields returned from corellium API

```kotlin
data class Project(
    val id: String,
    val name: String,
    val quotas: Quotas,
    val quotasUsed: Quotas
)

data class Quotas(
    val cores: Int,
    val instances: Int,
    val ram: Int,
    val cpus: Int,
    val gpus: Int? = null,
    val instance: Int? = null
)
```

### data class Instance

----
**NOTE**: Does not contain all fields returned from corellium API

```kotlin
data class Instance(
    val id: String = "",
    val name: String = "",
    val key: String = "",
    val flavor: String,
    val type: String = "",
    val project: String,
    val state: String = "",
    val bootOptions: BootOptions = BootOptions(),
    val patches: List<String> = emptyList(),
    val os: String = "",
    val osbuild: String = "",
    val agent: InstanceAgent? = InstanceAgent()
)

data class BootOptions(
    val bootArgs: String = "",
    val restoreBootArgs: String = "",
    val udid: String = "",
    val ecid: String = ""
)

data class InstanceAgent(
    val hash: String = "",
    val info: String = ""
)
```

### sealed class LoggingLevel

----
Subclass names' are corresponding with ktor's
enum [LogLevel](https://api.ktor.io/1.5.1/io.ktor.client.features.logging/-log-level/index.html) values.

* `LoggingLevel.All` -> `LogLevel.ALL`
* `LoggingLevel.None` -> `LogLevel.NONE`
* `LoggingLevel.Body` -> `LogLevel.BODY`
* `LoggingLevel.Headers` -> `LogLevel.HEADERS`
* `LoggingLevel.Info` -> `LogLevel.INFO`

### enum VPN

----

```kotlin
enum class VPN {
    OVPN, TBLK
}
```

## Additional Info

* There are lots of more features available by API, the current implementation has essential features to run iOS test on
  Corellium device, from Flank's perspective
* All `Corellium` client requests have a retry mechanism implemented. Sometimes it's not enough though
* Creating new instance usually requires USBFlux restart
