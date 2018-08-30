package ftl.json

import ftl.util.Outcome

class MatrixMap(
    val map: MutableMap<String, SavedMatrix>,
    val runPath: String
) {
    // determine success by FTL API, not junit xml.
    // FTL sometimes generates empty XML reports (0 failures) for a failed matrix
    fun allSuccessful(): Boolean {
        val savedMatrices = map.values
        var successful = true

        savedMatrices.forEach { matrix ->
            if (matrix.outcome != Outcome.success) successful = false
        }

        return successful
    }
}
