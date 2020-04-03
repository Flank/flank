package ftl.util

import ftl.json.SavedMatrix

sealed class FlankException(message: String? = null) : Throwable(message)
class FailedMatrix(val matrices: List<SavedMatrix>) : FlankException()
class FTLError(val matrix: SavedMatrix) : FlankException()
class YmlValidationError : FlankException()
class FlankTimeoutError(val map: Map<String, SavedMatrix>?, val projectId: String) : FlankException()
class FlankFatalError(message: String) : FlankException(message)
