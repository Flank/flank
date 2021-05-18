package flank.corellium.shard

/**
 * Obfuscate each test cases names.
 * Be aware that this function is not touching the structure,
 * just only hashing each [Shard.Test.Case.name] using alphabetical letters.
 * Use for security purpose.
 *
 * @receiver calculated shards
 * @return obfuscated shards
 */
fun obfuscate(shards: Shards): Shards =
    // Those nested mappings looks fearfully, but there are just a bunch of iterations where only the last one is important.
    shards.map { shard: List<Shard.App> ->
        shard.map { app: Shard.App ->
            app.copy(
                tests = app.tests.map { test: Shard.Test ->
                    test.copy(
                        cases = test.cases.map { case: Shard.Test.Case ->
                            // The only crucial operation which is making the result different than the input.
                            case.copy(name = obfuscationMappings.obfuscateAndroidTestName(case.name))
                        }
                    )
                }
            )
        }
    }

internal val obfuscationMappings: ObfuscationMappings = mutableMapOf()
