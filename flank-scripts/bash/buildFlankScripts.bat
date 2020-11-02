SET DIR=%~dp0

SET FLANK_SCRIPTS=%DIR%\..
SET GRADLE_EXECUTABLE_PATH=%FLANK_SCRIPTS%\..

%GRADLE_EXECUTABLE_PATH%\gradlew.bat flank-scripts:clean flank-scripts:assemble flank-scripts:shadowJar
copy %FLANK_SCRIPTS%\build\libs\flankScripts.jar %DIR%\flankScripts.jar
