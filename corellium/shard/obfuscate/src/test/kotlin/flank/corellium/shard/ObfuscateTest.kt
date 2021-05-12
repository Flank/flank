package flank.corellium.shard

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Test

class ObfuscateTest {

    private val shards: List<List<Shard.App>> =
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

    @Test
    fun test() {
        val obfuscatedShards = shards.obfuscate()

        assertEquals(shards.size, obfuscatedShards.size)

        shards.forEachIndexed { shardIndex, apps ->
            val obfuscatedApps = obfuscatedShards[shardIndex]

            assertEquals(apps.size, obfuscatedApps.size)

            apps.forEachIndexed { appIndex, app ->
                val obfuscatedApp = obfuscatedApps[appIndex]

                assertEquals(app.tests.size, obfuscatedApp.tests.size)
                assertEquals(app.name, obfuscatedApp.name)

                app.tests.forEachIndexed { testIndex, test ->
                    val obfuscatedTest = obfuscatedApp.tests[testIndex]

                    assertEquals(test.cases.size, obfuscatedTest.cases.size)
                    assertEquals(test.name, obfuscatedTest.name)

                    test.cases.forEachIndexed { caseIndex, case ->
                        val obfuscatedCase = obfuscatedTest.cases[caseIndex]

                        println("${case.name} -> ${obfuscatedCase.name}")
                        assertNotEquals(case.name, obfuscatedCase.name)
                    }
                }
            }
        }
    }
}
