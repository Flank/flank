SET DIR=%~dp0
SET scriptsJar=%DIR%flank-scripts.jar

if not exist %scriptsJar% (
    CALL %DIR%\buildFlankScripts.bat
)

java -jar "%scriptsJar%" %*
