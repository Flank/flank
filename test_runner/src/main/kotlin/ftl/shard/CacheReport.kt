package ftl.shard

import ftl.util.FlankTestMethod
import kotlin.math.roundToInt

object CacheReport {
    fun printCacheInfo(testsToRun: List<FlankTestMethod>, previousMethodDurations: Map<String, Double>) {
        val allTestCount = testsToRun.size
        val cacheMiss = calculateCacheMiss(testsToRun, previousMethodDurations)
        val cacheHit = cacheHit(allTestCount, cacheMiss)
        println()
        println("  Smart Flank cache hit: ${cachePercent(allTestCount, cacheHit).roundToInt()}% (${cacheHit} / ${allTestCount})")
    }

    private fun calculateCacheMiss(testsToRun: List<FlankTestMethod>, previousMethodDurations: Map<String, Double>): Int {
        return testsToRun.count { !previousMethodDurations.containsKey(it.testName) }
    }
    
    private fun cacheHit(allTestCount: Int, cacheMiss: Int) = allTestCount - cacheMiss

    private fun cachePercent(allTestCount: Int, cacheHit: Int): Double = if (allTestCount == 0) 0.0 else cacheHit.toDouble() / allTestCount * 100.0
}