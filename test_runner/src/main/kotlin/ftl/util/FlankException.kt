package ftl.util

import ftl.json.SavedMatrix

/**
 * Base class for all custom flank's exceptions
 */
sealed class FlankException(message: String? = null) : Throwable(message)

/**
 * Thrown when all matrices are finished and at least one test failed/is inconclusive.
 *
 * Exit code: 1
 *
 * @param matrices [List]<[SavedMatrix]> List of failed matrices
 */
class FailedMatrix(val matrices: List<SavedMatrix>, val ignoreFailed: Boolean = false) : FlankException()

/**
 * Thrown when at least one matrix is not finished.
 * Usually indicates Firebase TestLab internal error
 *
 * Exit code: 3
 *
 * @param matrix [SavedMatrix] Not finished matrix (with matrix state different then FINISHED)
 */
class FTLError(val matrix: SavedMatrix) : FlankException()

/**
 * Thrown when doctor command found an error in yml fail and wa unable to fix it
 *
 * Exit code: 1
 */
class YmlValidationError : FlankException()

/**
 * Thrown when flank run timeouted. This is flank itself timeout, not FTL.
 *
 * Exit code: 1
 *
 * @param map [Map]<[String], [SavedMatrix]>? map of matrices to be cancelled
 * @param projectId [String] id of flank's project
 */
class FlankTimeoutError(val map: Map<String, SavedMatrix>?, val projectId: String) : FlankException()

/**
 * Usually indicates missing or wrong usage of flags, incorrect parameters, errors in config files.
 *
 * Exit code: 2
 *
 * @param message [String] message to be printed to [System.err]
 */
class FlankFatalError(message: String) : FlankException(message)

/**
 * Common flank exception
 *
 * Exit code: 1
 *
 * @param message [String] message to be printed to [System.err]
 */
class FlankCommonException(message: String) : FlankException(message)

/**
 *  Exception throws when required parameters is missing
 *
 *  Exit code: 1
 *
 *  @param message [String] message to be printed to [System.err]
 */
class FlankConfigurationException(message: String) : FlankException(message)
