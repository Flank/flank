# Test count and smart sharding count don't match

Bug reported in [this issue](https://github.com/Flank/flank/issues/986)

## The problem

Flank does not support parameterized tests sharding. Every class with parameterized is considered as one test during shard calculation.

Flank is using [DEX parser](https://github.com/linkedin/dex-test-parser) to decompile apks and gather info about all the tests inside. As for now, Flank is unable to determine how many times a test in a parameterized class is invoked. Due to this fact scans apks for any class with an annotation that contains `JUnitParamsRunner` or `Parameterized`:

```kotlin

@RunWith(JUnitParamsRunner::class)
...
@RunWith(Parameterized::class)

```

## Solution

1. Flank knows how many tests and classes are being sent to Firebase. So we can inform the user of how many classes we have. Example:

```txt

 Smart Flank cache hit: 0% (0 / 9)
  Shard times: 240s, 240s, 240s, 360s

  Uploading app-debug.apk .
  Uploading app-multiple-flaky-debug-androidTest.apk .
  5 tests + 4 parameterized classes / 4 shards

```

1. Default test time for classes should be different from the default time for test
   1. You can set default test time for class with ```--default-class-test-time``` command
   2. If you did not set this time, the default value is 240s
