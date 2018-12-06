# Smart Flank Design Doc

Smart Flank is a sharding algorithm that groups tests into equally sized buckets.

## Current implementation

At the start of a run, Flank checks to see if there's a JUnit XML with timing info from the previous run. If there's no previous test time recorded, then the test is estimated to be 10 seconds. The tests are then grouped into equally timed buckets. The bucket count is set by the user provided `testShards` count.

After each test run, the aggregated JUnit XML from the new run is merged with the old run. Any tests not in the new run are discarded. Tests that were skipped, errored, failed, or empty are discarded. The test time value is recorded using the latest run. If a test failed in the new run and passed in the old run, the timing info from the old run is carried over.

The merged XML is uploaded to the user defined `smartFlankGcsPath`. If `smartFlankGcsPath` is not defined, then the smart flank feature will not activate.

Example:

```xml
<!-- Old Run -->
<?xml version='1.0' encoding='UTF-8' ?>
<testsuites>
  <testsuite name="EarlGreyExampleSwiftTests" tests="4" failures="1" errors="0" skipped="0" time="51.773" hostname="localhost">
    <testcase name="a()" classname="a" time="5.0"/>
    <testcase name="b()" classname="b" time="6.0"/>
    <testcase name="c()" classname="c" time="7.0"/>
    <testcase name="d()" classname="d" time="8.0"/>
  </testsuite>
</testsuites>
```

```xml
<!-- New run -->
<?xml version='1.0' encoding='UTF-8' ?>
<testsuites>
  <testsuite name="EarlGreyExampleSwiftTests" tests="4" failures="1" errors="0" skipped="0" time="51.773" hostname="localhost">
    <testcase name="a()" classname="a" time="1.0"/>
    <testcase name="b()" classname="b" time="2.0"/>
    <testcase name="c()" classname="c" time="0.584">
      <failure>Exception: NoMatchingElementException</failure>
      <failure>failed: caught "EarlGreyInternalTestInterruptException", "Immediately halt execution of testcase"</failure>
    </testcase>
    <testcase name="d()" classname="d" time="0.0">
        <skipped/>
    </testcase>
  </testsuite>
</testsuites>
```

```xml
<!-- Merged -->
<?xml version='1.0' encoding='UTF-8' ?>
<testsuites>
  <testsuite name="EarlGreyExampleSwiftTests" tests="3" failures="0" errors="0" skipped="0" time="10.0" hostname="localhost">
    <testcase name="a()" classname="a" time="1.0"/>
    <testcase name="b()" classname="b" time="2.0"/>
    <testcase name="c()" classname="c" time="7.0"/>
  </testsuite>
</testsuites>
```

## Issues

* Merging XML files is complicated
* A local run of 1 test will upload a new XML file that contains only that one test. That will discard timing info for all other tests.

## Ideas for improvement

* Keep a user configurable rolling number of aggregated xmls (1.xml, 2.xml, 3.xml) and shard based on the average time. Average time is expected to be more reliable than always using the last time in isolation.
* Identify a way of translating app binary to a default xml name (bundle id/package name) so that smart flank works out of the box for users.
* Provide an option to disable uploading so that local runs don't update time information for CI/pull request runs.
