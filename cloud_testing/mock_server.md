# Mock Server

Goal: Test Flank using a mock server so we don't have to spend $$ to verify the test runner works.

## Proof of concepts

### prism

- [prism](https://github.com/stoplightio/prism) consumes Swagger v2 and uses [json-schema-faker](https://github.com/json-schema-faker/json-schema-faker) to return generated default responses. The fields are randomly null which crashes the runner. Prism is [exceptionally slow](https://github.com/stoplightio/prism/issues/123) and closed sourced. There's no way to customize the behavior. Not a viable option.

### `mock_server_inflector`

- [swagger-codegen](https://github.com/swagger-api/swagger-codegen/releases/tag/v3.0.0-rc0) consumes OpenAPI v3 and generates a fake server using [swagger-inflector](https://github.com/swagger-api/swagger-inflector). Static canned responses are used based on the OpenAPI description. The responses aren't useful since they don't mimic a real server.  Swagger generates a massive amount of code. The [build system lacks gradle support](https://github.com/swagger-api/swagger-codegen/issues/7578).

### `mock_server_vertx`

- [vertx-web-api-contract](https://github.com/vert-x3/vertx-web/tree/master/vertx-web-api-contract) consumes OpenAPI v3 and generates a fake server using [slush-vertx](https://github.com/pmlopes/slush-vertx). Stub implementations for routes are provided. There are no precanned responses. Unfortunately the code generation is incompatible with the variable naming used in Firebase Test Lab. I opened [an issue upstream](https://github.com/vert-x3/vertx-web/issues/831). Of the three generated options, this looks like the nicest one. However the Google API surface is enormous and we don't need stubs for everything.

### `mock_server`

- [ktor](http://ktor.io/) is a nice Kotlin server based on Netty. In a single file, all the necessary routes are implemented for each API. The routes are easy to define manually. The complexity of OpenAPI / code generation is avoided. This is the approach that's working on the `kotlin_poc` runner.

## Known issues

* The gcloud client still pings `https://accounts.google.com/o/oauth2/token` in mocked mode.
* Using a flag for mocked mode isn't ideal. Dependency injection with interfaces would be more robust.
