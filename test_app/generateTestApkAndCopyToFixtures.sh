./gradlew assembleAndroidTest

if [ "$1" != "" ]; then
    path=/$1
fi

mkdir -p ../test_runner/src/test/kotlin/ftl/fixtures/tmp/apks$path
find . -type f -name "*androidTest.apk" -not -name "*-error-debug-androidTest.apk" -exec cp {} ../test_runner/src/test/kotlin/ftl/fixtures/tmp/apks$path \;