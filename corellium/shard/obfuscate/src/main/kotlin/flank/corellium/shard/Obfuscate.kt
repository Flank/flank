package flank.corellium.shard

/**
 * Obfuscate each [Shard.Test.Case.name] value.
 * Use for security purpose.
 *
 * @receiver calculated shards
 * @return obfuscated shards
 */
fun Shards.obfuscate(): Shards =
    map { shard ->
        shard.map { app ->
            app.copy(tests = app.tests.map { test ->
                test.copy(cases = test.cases.map { case ->
                    case.copy(name = obfuscationMappings.obfuscateAndroidTestName(case.name))
                })
            })
        }
    }


internal val obfuscationMappings: ObfuscationMappings = mutableMapOf()
