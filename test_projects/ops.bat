SET ARG=%1

if %ARG%=="android" CALL $DIR/../flank-scripts/bash/flankScripts.bat shell ops android --copy --generate

if %ARG%=="ios" echo "iOS Build on windows not supported

if %ARG%=="go" CALL $DIR/../flank-scripts/bash/flankScripts.bat shell ops go --copy --generate

if %ARG%=="all" (
    CALL $DIR/../flank-scripts/bash/flankScripts.bat shell ops android --copy --generate
    CALL $DIR/../flank-scripts/bash/flankScripts.bat shell ops go
)
