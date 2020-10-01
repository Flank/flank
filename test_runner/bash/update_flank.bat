
for %%F in (%filename%) do set dirname=%%~dpF
echo "%dirname%"
cd ..
cd ..
call gradlew.bat clean assemble shadowjar
cd test_runner\bash
