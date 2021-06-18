# Shard dump

Allows dumping shards as formatted json file.

### References

* Module type - [tool](../../../docs/architecture.md#tool)
* Dependency type - [static](../../../docs/architecture.md#static_dependencies)
* Public API - [Dump.kt](./src/main/kotlin/flank/corellium/shard/Dump.kt)

## Example

For the example input structure:

```kotlin
val input: List<List<Shard.App>> =
    listOf(
        listOf(
            Shard.App(
                name = "app1",
                tests = listOf(
                    Shard.Test(
                        name = "app1-test1",
                        cases = listOf(
                            Shard.Test.Case(
                                name = "app1.test1.Test1#case1",
                                duration = 10_000,
                            ),
                        )
                    ),
                )
            ),
        ),
        listOf(
            Shard.App(
                name = "app1",
                tests = listOf(
                    Shard.Test(
                        name = "app1-test1",
                        cases = listOf(
                            Shard.Test.Case(
                                name = "app1.test1.Test1#case2",
                                duration = 2_000,
                            ),
                        )
                    ),
                )
            ),
            Shard.App(
                name = "app2",
                tests = listOf(
                    Shard.Test(
                        name = "app2-test1",
                        cases = listOf(
                            Shard.Test.Case(
                                name = "app2.test1.Test2#case1",
                                duration = 1_000,
                            ),
                        )
                    ),
                    Shard.Test(
                        name = "app2-test1",
                        cases = listOf(
                            Shard.Test.Case(
                                name = "app2.test1.Test2#case2",
                            ),
                        )
                    ),
                )
            ),
        )
    )
```

The call of:

```kotlin
input.dumpToFile("path/to/file.json")
```

Should create json file under the path `path/to/file.json` that contains: 
```json
[
  [
    {
      "name": "app1",
      "tests": [
        {
          "name": "app1-test1",
          "cases": [
            {
              "name": "app1.test1.Test1#case1",
              "duration": 10000
            }
          ]
        }
      ]
    }
  ],
  [
    {
      "name": "app1",
      "tests": [
        {
          "name": "app1-test1",
          "cases": [
            {
              "name": "app1.test1.Test1#case2",
              "duration": 2000
            }
          ]
        }
      ]
    },
    {
      "name": "app2",
      "tests": [
        {
          "name": "app2-test1",
          "cases": [
            {
              "name": "app2.test1.Test2#case1",
              "duration": 1000
            }
          ]
        },
        {
          "name": "app2-test1",
          "cases": [
            {
              "name": "app2.test1.Test2#case2",
              "duration": 120
            }
          ]
        }
      ]
    }
  ]
]
```
