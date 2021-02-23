## About

Kotlin-based, naive, non-blocking client for flank-corellium integration purposes. It's based
on [corellium-api](https://github.com/corellium/corellium-api). It has only essential features implemented to be able to
run simple iOS test run similar
to [example](https://github.com/corellium/corellium-api/blob/master/examples/agent-simple.js)

## Installing

JDK 8+ is required (tested on java 8 & 15)

To build just run (from the root of the project)

```
./gradlew build -p corellium/corellium-client
```

## API

### class Corellium

-------

#### new instance

Creates new corellium client instance. Options:

* `api` -> your corellium's http adrress, example: `yourcompany.enterprise.corellium.com` (without `https`)
* `username`, `password` -> credentials to log in and acquire token
* `tokenFallback` (optional) -> pass if you don't want to log in
* `logging` (optional) -> logging level for http requests, default: `None`. More info [LoggingLevel](#sealed-class-logginglevel)

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

Logs in with given credentials. Acquired `token` is persisted inside the client. It is also returned from the function if
you want to reuse it

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

Creates new [Instance](#data-class-instance), returns newly created instance id.

NOTE: instance created with this method is not ready to use (state ==  `creating`).
Use [waitUntilInstanceIsReady](#waituntilinstanceisreadyinstanceid-string) to ensure VM has `on` state

Following options are required (all `Instance` fields are described [here](#data-class-instance))

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
Waits until instance with `id` == `instanceId` is ready to use (`state` is `on`). It's `suspend` function so the thread is not blocked.

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

**NOTE**: Creating an agent can be flaky sometimes and may throw 5xx errors. There is the retry mechanism implemented but sometimes it's not enough.

```kotlin
val instanceId = "11111-aaaaa-bbbbb-33333"
val instance = client.getInstanceInfo(instanceId)
val agent: Agent = client.createAgent(instance.agent.info)
```

#### getVPNConfig(projectId: String, type: VPN)

### class Agent

#### new instance

#### uploadFile(path: String, bytes: ByteArray)

#### close()

### data class Project

### data class Instance

### sealed class LoggingLevel

### enum VPN
