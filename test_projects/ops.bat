@ECHO OFF
SET ARG=%1
if "%ARG%" == "android" (
	CALL ../flank-scripts/bash/flankScripts.bat shell ops android --copy --generate
)

if "%ARG%" == "ios" (
    echo iOS Build on windows not supported
)

if "%ARG%" == "go" (
	CALL ../flank-scripts/bash/flankScripts.bat shell ops go
)

if "%ARG%" == "all" (
    CALL ../flank-scripts/bash/flankScripts.bat shell ops android --copy --generate
    CALL ../flank-scripts/bash/flankScripts.bat shell ops go
)
