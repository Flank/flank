# Flank exit codes and exceptions

Exit code  | Returned by exception | Description
 --        |                    -- |            -- |
0          | | All tests passed
1          | FlankGeneralError, FlankTimeoutError | A general failure occurred. Possible causes include: a filename that does not exist or an HTTP/network error.
2          | FlankConfigurationError, YmlValidationError | Usually indicates missing or wrong usage of flags, incorrect parameters, errors in config files.
3          | FlankNoTestsError | No tests detected in the test apk, or they have been filtered out. 
10         | FailedMatrixError | At least one matrix not finished (usually a FTL internal error) or unexpected error occurred.
15         | FTLError | Firebase Test Lab could not determine if the test matrix passed or failed, because of an unexpected error.
18         | IncompatibleTestDimensionError | The test environment for this test execution is not supported because of incompatible test dimensions. This error might occur if the selected Android API level is not supported by the selected device type.
19         | MatrixCanceledError | The test matrix was canceled by the user.
20         | InfrastructureError | A test infrastructure error occurred.
