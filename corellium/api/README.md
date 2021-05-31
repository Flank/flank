# Flank - Corellium - API

This module specifies the API required by the business logic to operate on corellium features. 

The API is designed to exactly meet domain requirements rather than reflect the remote protocol or third-party client.

### References

* Module type - [API](../../docs/architecture.md#api)
* Dependency type - [dynamic](../../docs/architecture.md#dynamic_dependencies)
* Public API - [CorelliumApi.kt](./src/main/kotlin/flank/corellium/api/CorelliumApi.kt)

## Features

* Authorizing corellium session - [Authorization.kt](./src/main/kotlin/flank/corellium/api/Authorization.kt)
* Invoking android virtual instances - [AndroidInstance.kt](./src/main/kotlin/flank/corellium/api/AndroidInstance.kt)
* Installing android apps and tests - [AndroidApps.kt](./src/main/kotlin/flank/corellium/api/AndroidApps.kt)
* Executing tests & getting logs - [AndroidTestPlan.kt](./src/main/kotlin/flank/corellium/api/AndroidApps.kt)
