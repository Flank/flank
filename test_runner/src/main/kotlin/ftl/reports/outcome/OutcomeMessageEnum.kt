package ftl.reports.outcome

enum class OutcomeMessageEnum(val message: String) {
    MALFORMED_APK("The app APK is not a valid Android application"),
    MALFORMED_TEST_APK("The test APK is not a valid Android instrumentation test"),
    NO_MANIFEST("The app APK is missing the manifest file"),
    NO_PACKAGE_NAME("The APK manifest file is missing the package name"),
    TEST_SAME_AS_APP("The test APK has the same package name as the app APK"),
    NO_INSTRUMENTATION("The test APK declares no instrumentation tags in the manifest"),
    NO_SIGNATURE("At least one supplied APK file has a missing or invalid signature"),
    INSTRUMENTATION_ORCHESTRATOR_INCOMPATIBLE(
        "The test runner class specified by the user or the test APK's " +
            "manifest file is not compatible with Android Test Orchestrator. " +
            "Please use AndroidJUnitRunner version 1.0 or higher"
    ),
    NO_TEST_RUNNER_CLASS(
        "The test APK does not contain the test runner class specified by " +
            "the user or the manifest file. The test runner class name may be " +
            "incorrect, or the class may be mislocated in the app APK."
    ),
    NO_LAUNCHER_ACTIVITY("The app APK does not specify a main launcher activity"),
    FORBIDDEN_PERMISSIONS("The app declares one or more permissions that are not allowed"),
    INVALID_ROBO_DIRECTIVES("Cannot have multiple robo-directives with the same resource name"),
    INVALID_DIRECTIVE_ACTION("Robo Directive includes at least one invalid action definition."),
    INVALID_RESOURCE_NAME("Robo Directive resource name contains invalid characters( ' (colon), or \" \" (space)"),
    TEST_LOOP_INTENT_FILTER_NOT_FOUND("The app does not have a correctly formatted game-loop intent filter"),
    SCENARIO_LABEL_NOT_DECLARED("A scenario-label was not declared in the manifest file"),
    SCENARIO_LABEL_MALFORMED("A scenario-label in the manifest includes invalid numbers or ranges"),
    SCENARIO_NOT_DECLARED("A scenario-number was not declared in the manifest file"),
    DEVICE_ADMIN_RECEIVER("Device administrator applications are not allowed"),
    MALFORMED_XC_TEST_ZIP(
        "The XCTest zip file was malformed. The zip did not contain a single " +
            ".xctestrun file and the contents of the DerivedData/Build/Products " +
            "directory."
    ),
    BUILT_FOR_IOS_SIMULATOR("The provided XCTest was built for the iOS simulator rather than for a physical device"),
    NO_TESTS_IN_XC_TEST_ZIP("The .xctestrun file did not specify any test targets to run"),
    USE_DESTINATION_ARTIFACTS(
        "One or more of the test targets defined in the .xctestrun file " +
            "specifies \"UseDestinationArtifacts\"), which is not allowed"
    ),
    TEST_NOT_APP_HOSTED(
        "One or more of the test targets defined in the .xctestrun file " +
            "does not have a host binary to run on the physical iOS device), " +
            "which may cause errors when running xcodebuild"
    ),
    NO_CODE_APK("\"hasCode\" is false in the Manifest. Tested APKs must contain code"),
    INVALID_INPUT_APK("Either the provided input APK path was malformed, the APK file does not exist, or the user does not have permission to access the file"),
    INVALID_APK_PREVIEW_SDK("Your app targets a preview version of the Android SDK that's incompatible with the selected devices."),
    PLIST_CANNOT_BE_PARSED("One or more of the Info.plist files in the zip could not be parsed"),
    INVALID_PACKAGE_NAME("The APK application ID (aka package name), is invalid. See also https(//developer.android.com/studio/build/application-id"),
    MALFORMED_IPA("The app IPA is not a valid iOS application"),
    MISSING_URL_SCHEME("The iOS game loop application does not register the custom URL scheme"),
    MALFORMED_APP_BUNDLE("The iOS application bundle (.app), is invalid"),
    UNKNOWN("Unknown error")
}

fun getOutcomeMessageByKey(key: String) = try {
    OutcomeMessageEnum.valueOf(key).message
} catch (error: IllegalArgumentException) {
    "UNKNOWN"
}
