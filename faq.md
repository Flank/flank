## How do I specify the path to flank.yml?

By default flank will look for `flank.yml` when running Android tests and `flank.ios.yml` when running iOS tests.
A custom path may be provided using a CLI flag: `java -jar flank.jar android run --config=custom/path/config.yml`

For all supported CLI flags, use the help flag: `java -jar flank.jar android run --help`

## I'm using max-test-shards set to -1 to shard each test, why is there only one shard?

When `shard-time` is used, Flank will pack tests into equally timed shards up to a maximum of `max-test-shards`.
The timing data comes from the `smart-flank-gcs-path` file. On a fresh run, that file doesn't exist.
Tests are assumed to be [10 seconds][1] when there's no previous recorded time for that test.

In the example of `shard-time: 120`, that means 12 tests will be used per shard on a fresh run with no timing data.
On the second run, the measured time will be used instead of 10 seconds.

```yaml
flank:
  max-test-shards: -1
  shard-time: 120
```

To always shard each test on a new device, omit `shard-time`.

[1]: https://github.com/TestArmada/flank/blob/6d128ce949247ea3b5066677a220b93b028ae77f/test_runner/src/main/kotlin/ftl/shard/Shard.kt#L92
