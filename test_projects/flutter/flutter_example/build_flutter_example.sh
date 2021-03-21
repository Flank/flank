flutter build apk
dir=$(pwd)
pushd android

./gradlew app:assembleAndroidTest

./gradlew app:assembleDebug -Ptarget=$dir"/integration_tests/integration_tests.dart"

popd
