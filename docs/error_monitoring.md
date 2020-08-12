# Flank Error Monitoring

Flank makes use of Bugsnag as an Error monitoring and project stability tool. 

For more information about Bugsnag please visit its website [here](https://www.bugsnag.com/) and documentation [here](https://docs.bugsnag.com/)

## Control

It is possible to disable the Bugsnag integration used by Flank. Flank makes use of the same OPT-OUT approach as Gcloud CLI and can be disabled the same way.

 - To disable, place ONLY the word ```DISABLED``` in the file ````~/.gsutil/analytics-uuid```` and Flank will no longer make use of Bugsnag integration.
   - It is recommended to backup the file ````~/.gsutil/analytics-uuid```` if it exists as it may contain your project specific UUID.
   - For example running the following code will disable Bugsnag: ```echo "DISABLED" > ~/.gsutil/analytics-uuid``` (make sure the file exists!)

 - To enable or reenable bugsnag simply remove the file ```~/.gsutil/analytics-uuid``` or replace it with your backed-up file and the project will again begin to make use Bugsnag.


 ## More information

To see how Bugsnag is integrated within the Flank project please see the Flank Bugsnag testcase [here](../test_runner/src/test/kotlin/ftl/util/FlankBugsnagInitHelperTest.kt) and the actual Bugsnag implementation found [here](../test_runner/src/main/kotlin/ftl/util/BugsnagInitHelper.kt)

Flank makes use of Bugsnag to monitor test runner stability and to capture errors. It provides diagnostic data that is used to make data driven decisions when prioritizing fixing bugs or adding new features.

The goal is to use language that demonstrates value to Flank's customers.

For example American express wants a stable and reliable test runner. Bugsnag is helping us deliver this too them.


BugSnag sumarizes the following about how it makes use of the data it collects and stores:

 - Bugsnag monitors web, mobile and server applications and programs under the direction of its Customers, to provide error Event Data about the Customer’s applications and programs to the   Customer. Event Data may contain Personal Information of the individuals who use the Customer’s applications. Bugsnag itself does not collect this end user Personal Information and Bugsnag has no direct relationship with these individuals. For example, a Customer may direct Bugsnag to log certain software application’s end user information associated with Event Data, such as the end user’s device ID, email and name. Bugsnag is a data processor and follows the instructions of the Customer in these cases.

More information about Bugsnag data policy can be found [here](https://docs.bugsnag.com/legal/privacy-policy/#:~:text=Services%20via%20Mobile%20Devices.&text=Bugsnag%20will%20collect%20certain%20information,operating%20system%20of%20your%20device.)


