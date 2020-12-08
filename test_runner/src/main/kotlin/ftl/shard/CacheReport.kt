package ftl.shard

import ftl.log.logLn
import ftl.util.FlankTestMethod
import kotlin.math.roundToInt

fun printCacheInfo(testsToRun: List<FlankTestMethod>, previousMethodDurations: Map<String, Double>) {
    val allTestCount = testsToRun.size
    val cacheHit = cacheHit(allTestCount, calculateCacheMiss(testsToRun, previousMethodDurations))
    val cachePercent = cachePercent(allTestCount, cacheHit)
    logLn()
    logLn(" Smart Flank cache hit: ${cachePercent.roundToInt()}% ($cacheHit / $allTestCount)")
}

private fun cacheHit(allTestCount: Int, cacheMiss: Int) = allTestCount - cacheMiss

private fun calculateCacheMiss(testsToRun: List<FlankTestMethod>, previousMethodDurations: Map<String, Double>): Int {
    return testsToRun.count { !previousMethodDurations.containsKey(it.testName) }
}

private fun cachePercent(allTestCount: Int, cacheHit: Int): Double =
    if (allTestCount == 0) 0.0 else cacheHit.toDouble() / allTestCount * 100.0
