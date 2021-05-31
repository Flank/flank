# Shard obfuscation

Allows obfuscating test cases names for security reasons.

### References

* Module type - [tool](../../../docs/architecture.md#tool)
* Dependency type - [static](../../../docs/architecture.md#static_dependencies)
* Public API - [Parser.kt](./src/main/kotlin/flank/corellium/shard/Obfuscate.kt)

### Example

Obfuscation will change the test cases names as following:

```
app1.test1.Test1#case1 -> a.a.A#a
app1.test1.Test1#case2 -> a.a.A#b
app2.test1.Test2#case1 -> b.a.A#a
app2.test1.Test2#case2 -> b.a.A#b
```

So, for the example input structure:

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
input.obfuscate()
```

Should return result equal to the following:

```kotlin
val output: List<List<Shard.App>> =
    listOf(
        listOf(
            Shard.App(
                name = "app1",
                tests = listOf(
                    Shard.Test(
                        name = "app1-test1",
                        cases = listOf(
                            Shard.Test.Case(
                                name = "a.a.A#a",
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
                                name = "a.a.A#b",
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
                                name = "b.a.A#a",
                                duration = 1_000,
                            ),
                        )
                    ),
                    Shard.Test(
                        name = "app2-test1",
                        cases = listOf(
                            Shard.Test.Case(
                                name = "b.a.A#b",
                            ),
                        )
                    ),
                )
            ),
        )
    )
```
