package ftl.run.exception

import ftl.api.TestMatrix
import java.io.IOException
import java.lang.Exception

/**
 * Base class for all custom flank's exceptions
 */
sealed class FlankException(message: String? = null, cause: Throwable? = null) : Throwable(message, cause)

/**
 * Thrown when all matrices are finished and at least one test failed/is inconclusive.
 *
 * Exit code: 10
 *
 * @param matrices [List]<[SavedMatrix]> List of failed matrices
 */
class FailedMatrixError(val matrices: List<TestMatrix.Data>, val ignoreFailed: Boolean = false) : FlankException()

/**
 * Thrown when at least one matrix is not finished.
 * Usually indicates Firebase TestLab internal error
 *
 * Exit code: 15
 *
 * @param matrix [SavedMatrix] Not finished matrix (with matrix state different then FINISHED)
 */
open class FTLError(val matrix: TestMatrix.Data) : FlankException()

/**
 * Thrown when doctor command found an error in yml fail and wa unable to fix it
 *
 * Exit code: 2
 */
class YmlValidationError(message: String? = null) : FlankException(message)

/**
 * Thrown when flank run timeouted. This is flank itself timeout, not FTL.
 *
 * Exit code: 1
 *
 * @param map [Map]<[String], [SavedMatrix]>? map of matrices to be cancelled
 * @param projectId [String] id of flank's project
 */
class FlankTimeoutError(val map: Map<String, TestMatrix.Data>?, val projectId: String) : FlankException()

/**
 * Usually indicates missing or wrong usage of flags, incorrect parameters, errors in config files.
 *
 * Exit code: 2
 *
 * @param message [String] message to be printed to [System.err]
 */
class FlankConfigurationError : FlankException {
    constructor(message: String) : super(message)
    constructor(cause: Exception) : super(cause = cause)
    constructor(message: String, cause: Exception) : super(message, cause)
}

/**
 * A general failure occurred. Possible causes include: a filename that does not exist or an HTTP/network error.
 *
 * Exit code: 1
 *
 * @param message [String] message to be printed to [System.err]
 */
class FlankGeneralError : FlankException {
    constructor(message: String) : super(message)
    constructor(cause: Throwable) : super(cause = cause)
    constructor(message: String, cause: Throwable) : super(message, cause)
}

/**
 * No tests found. The tests apk is empty or tests have been filtered out.
 *
 * Exit code: 3
 *
 * @param message [String] message to be printed to [System.err]
 */
class FlankNoTestsError(message: String? = null, cause: Throwable? = null) : FlankException(message, cause)

/**
 * Base class for project related exceptions.
 * Should be caught and rewrap to FlankGeneralError with project id info attached.
 *
 * @param exc [IOException]
 */
sealed class FTLProjectError(exc: IOException) : FlankException("Caused by: $exc")
class PermissionDenied(exc: IOException) : FTLProjectError(exc)
class ProjectNotFound(exc: IOException) : FTLProjectError(exc)
class FailureToken(exc: IOException) : FTLProjectError(exc)

/**
 * The test environment for this test execution is not supported because of incompatible test dimensions. This error might occur if the selected Android API level is not supported by the selected device type.
 *
 * Exit code: 18
 *
 * @param message [String] message to be printed to [System.err]
 */
class IncompatibleTestDimensionError(message: String) : FlankException(message)

/**
 * The test environment for this test execution is not supported because of incompatible test dimensions. This error might occur if the selected Android API level is not supported by the selected device type.
 *
 * Exit code: 18
 *
 * @param message [String] message to be printed to [System.err]
 */
class MatrixValidationError(message: String) : FlankException(message)

/**
 * The test matrix was canceled by the user.
 *
 * Exit code: 19
 *
 * @param message [String] message to be printed to [System.err]
 */
class MatrixCanceledError(message: String) : FlankException(message)

/**
 * A test infrastructure error occurred.
 *
 * Exit code: 20
 *
 * @param message [String] message to be printed to [System.err]
 */
class InfrastructureError(message: String) : FlankException(message)
