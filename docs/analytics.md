# Flank Analytics

Flank makes use of Bugsnag as its only analytics platform. It uses Bugsnag as an error monitor and project stability tool.

For more information about Bugsnag please visit its website [here](https://www.bugsnag.com/) and documentation [here](https://docs.bugsnag.com/)

## Control

It is possible to disable the Bugsnag analytics integration used by flank or to make use of custom Bugsnag key so that any analytics can be displayed for custom use.

 - To disable, place the word ```DISABLED``` in the file ````~/.gsutil/analytics-uuid```` and Flank will no longer send any analytics data
   - Or simply running the following code (make sure the file exists!) will disable: ```echo "DISABLED" > ~/.gsutil/analytics-uuid```

 - To enable or reenable bugsnag simply remove the file ```~/.gsutil/analytics-uuid``` and the project will default to make use of the flanks Bugsnag key

 - To make use of custom Bugsnag integration, place your bugsnag key/UUID in ```~/.gsutil/analytics-uuid``` and the flank project will make use of it instead of the default key.

 ## More information

 To see how Bugsnag integration is used within the project please see the Flank Bugsnag testcase [here](../test_runner/src/test/kotlin/ftl/util/FlankBugsnagInitHelperTest.kt)


