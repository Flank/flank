package flank.shard

import org.junit.After
import org.junit.Assert
import org.junit.Test
import java.io.File

class DumpTest {

    private val filePath = "dump-shards.json"
    private val file = File(filePath)

    private val input: List<List<Shard.App>> =
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

    private val expected = """
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
    """.trimIndent()

    @Test
    fun testDumpToFile() {
        input.dumpTo(file.writer())

        Assert.assertTrue(file.exists())
        Assert.assertEquals(expected, file.readText().trim())
    }

    @After
    fun cleanUp() {
        file.delete()
    }
}
