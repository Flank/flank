package ftl.util

import ftl.json.SavedMatrix
import java.io.IOException

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
 * Exit code: 15
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
 * Base class for project related exceptions.
 * Should be caught and rewrap to FlankCommonException with project id info attached.
 *
 * @param exc [IOException]
 */
sealed class FTLProjectError(exc: IOException) : FlankException("Caused by: $exc")
class PermissionDenied(exc: IOException) : FTLProjectError(exc)
class ProjectNotFound(exc: IOException) : FTLProjectError(exc)

/**
 * A general failure occurred. Possible causes include: a filename that does not exist or an HTTP/network error.
 *
 * Exit code: 1
 *
 * @param message [String] message to be printed to [System.err]
 */
class FlankGeneralFailure(message: String) : FlankException(message)

/**
 * The test environment for this test execution is not supported because of incompatible test dimensions. This error might occur if the selected Android API level is not supported by the selected device type.
 *
 * Exit code: 18
 *
 * @param message [String] message to be printed to [System.err]
 */
class IncompatibleTestDimension(message: String) : FlankException(message)

/**
 * The test matrix was canceled by the user.
 *
 * Exit code: 19
 *
 * @param message [String] message to be printed to [System.err]
 */
class MatrixCanceled(message: String) : FlankException(message)

/**
 * A test infrastructure error occurred.
 *
 * Exit code: 20
 *
 * @param message [String] message to be printed to [System.err]
 */
class InfrastructureError(message: String) : FlankException(message)
