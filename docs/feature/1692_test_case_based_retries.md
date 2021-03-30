# Test case based retries

## Description

Test case-based retires is a feature request which automatically retries failed tests.

## Things worth mention

- ["One thing that would be worth to keep in mind, since FTL does not support such logic it would require from flank creating new matrix (matrices) for failed tests. It's not something extremely difficult but there is another overhead. FTL will set up new device(s) and that takes some time. I think it's not a blocker but let's don't forget about it."(https://github.com/Flank/flank/issues/778#issuecomment-696027295)
- [please also make sure that the total test count tally is not impacted by the retry. If we have 100 tests that execute, and 2 are flaky and get re-ran twice, we should still report 100 tests. I saw this issue in FTL where they actually incremented the total test count, which threw off our jenkins test analyzer.](https://github.com/Flank/flank/issues/778#issuecomment-702922562)

## Solution

Failed test case retries could be done using an automatic solution that starts failing tests just after the previous test run finished. Also, there should be a possibility to manually run only failed tests based on the report from a previous run.



### Automatic

#### Run

- Flank config will contain a boolean option to retry failed tests. With using this option flank automatically create a new matrix with failed tests and run it after.

#### Implementation

- A new option is required to be added to enable the auto-retry. When the option is present Flank will check if there are any failed tests after the test run and retry it by creating a new matrix. After running the matrix test report needs to be updated with retried test results.

#### Result

- As a result, retried failed test run results will replace the first test run.

#### Possible problems

- The output should clearly show information that failed tests are re-executed.



### Manual

#### Run

- Flank will have a new command to run only failed tests. As an argument of this command report file should be appended.

#### Implementation

- To run manual retry failed test case-based retries new flank command needs to be added. As an additional Flank argument, the previous report file needs to be provided

#### Result

- As a result, we will get the new test run with a separate report.
