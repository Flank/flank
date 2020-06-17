./gradlew assembleAndroidTest

mkdir -p ../test_runner/src/test/kotlin/ftl/fixtures/tmp/apks
find . -type f -name "*androidTest.apk" -exec cp {} ../test_runner/src/test/kotlin/ftl/fixtures/tmp/apks \;