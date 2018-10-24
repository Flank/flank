iOS pass:

```xml
<testsuite name="EarlGreyExampleSwiftTests" hostname="localhost" tests="16" failures="0" errors="0" time="25.892">
  <testcase name="testBasicSelection()" classname="EarlGreyExampleSwiftTests" time="2.0"/>
  <system-out/>
  <system-err/>
```

iOS fail:

```xml
<testsuite name="EarlGreyExampleSwiftTests" hostname="localhost" tests="17" failures="1" errors="0" time="25.881">
<properties/>
<testcase name="testBasicSelectionAndAction()" classname="EarlGreyExampleSwiftTests" time="0.584">
<failure>
  ...
```

Android fail:

```xml
<testsuite name="" tests="2" failures="1" errors="0" skipped="0" time="3.87" timestamp="2018-09-09T00:16:36" hostname="localhost">
<properties/>
<testcase name="testFails" classname="com.example.app.ExampleUiTest" time="0.857">
<failure>
  ...
```

Android pass:

```xml
<testsuite name="" tests="1" failures="0" errors="0" skipped="0" time="2.278" timestamp="2018-09-14T20:45:55" hostname="localhost">
<properties/>
<testcase name="testPasses" classname="com.example.app.ExampleUiTest" time="0.328"/>
</testsuite>
```

------

results / analysis:

```
    testsuite
      name     - test target name
      tests    - count of total tests
      failures - count of failures (test assertion failed)
      errors   - count of errors (unhandled exceptions)
      time     - overall time of test suite in seconds
      hostname - always localhost
    testcase
      name      - name of test method
      classname - name of class that defines the test
      time      - time in seconds

  Android
    testsuite
      name - always empty string
      tests
      failures
      errors
      skipped* Android only.
      time
      timestamp* Android only.
      hostname - always localhost
    testcase
      name
      classname
      time
```