@ECHO OFF
SET ARG=%1
if "%ARG%" == "android" (
	CALL ../flank-scripts/bash/flankScripts.bat assemble android app --copy --generate
)

if "%ARG%" == "ios" (
    echo iOS Build on windows not supported
)

if "%ARG%" == "go" (
	CALL ../flank-scripts/bash/flankScripts.bat assemble go
)

if "%ARG%" == "all" (
    CALL ../flank-scripts/bash/flankScripts.bat assemble android app --copy --generate
    CALL ../flank-scripts/bash/flankScripts.bat assemble go
)
