# Custom sharding

##### NOTE: Currently for android only, iOS support will be released soon

With [#1665](https://github.com/Flank/flank/issues/1665) Flank received the new feature called `Custom Sharding`. It
enables Flank to consume predefined sharding and apply it during a test run. The feature gives flexibility and enables manual
optimization. It also allows users to set up different sharding per app-test apk pair (android only).

## Android

Below you can find an example flow with all features explained

### 1. Acquire dump shard for current configuration

Suppose you config with options:

`flank.yml`

```yml
gcloud:
  app: ./app-debug.apk
  robo-script: ./MainActivity_robo_script.json
flank:
  max-test-shards: 2
  additional-app-test-apks:
    - test: ./debug-1.apk
    - test: ../build-dir/debug-2.apk
    - test: gs://path/to/your/bucket/debug-3.apk
```

`flank firebase test android run -c=flank.yml` will run on 4 matrices:

* 1x Robo run
* 3x instrumentation tests with 2 shards max each

`flank firebase test android run -c=flank.yml --dump-shards` produces `android_shards.json` with sharding:

```json
{
  "matrix-0": {
    "app": "[PATH]/app-debug.apk",
    "test": "[PATH]/debug-1.apk",
    "shards": {
      "shard-0": [
        "class com.TestClassA#test1",
        "class com.TestClassA#test2",
        "class com.package2.TestClassB#test4"
      ],
      "shard-1": [
        "class com.TestClassA#test3",
        "class com.package2.TestClassB#test1",
        "class com.package2.TestClassB#test2",
        "class com.package2.TestClassB#test3"
      ]
    },
    "junit-ignored": [
      "class com.TestClassA#ignoredTest"
    ]
  },
  "matrix-1": {
    "app": "[PATH]/app-debug.apk",
    "test": "[PATH]/debug-2.apk",
    "shards": {
      "shard-0": [
        "class com.ParameterizedTest",
        "class com.package3.TestClass3#test5"
      ],
      "shard-1": [
        "class com.package3.TestClass3#test1",
        "class com.package3.TestClass3#test2",
        "class com.package3.TestClass3#test3",
        "class com.package3.TestClass3#test4"
      ]
    },
    "junit-ignored": [
      "class com.package.3.TestClassA#ignoredTest1",
      "class com.package.3.TestClassA#ignoredTest2"
    ]
  },
  "matrix-2": {
    "app": "[PATH]/app-debug.apk",
    "test": "[PATH]/debug-3.apk",
    "shards": {
      "shard-0": [
        "class com.package4.TestClass4#test1",
        "class com.package4.TestClass4#test2",
        "class com.package4.subpackage.TestClass6#test3"
      ],
      "shard-1": [
        "class com.package4.TestClass4#test3",
        "class com.package4.subpackage.TestClass6#test1",
        "class com.package4.subpackage.TestClass6#test2"
      ]
    },
    "junit-ignored": [
    ]
  }
}
```

### 2. Prepare custom sharding JSON file

Now you can make changes of your interest, flank will try to find corresponding by app-test pair names, and then apply
custom sharding.

1. for `debug-1.apk` let's add another shard and move `TestClassB#test4` & `TestClassB#test3` into it:

```json
{
  "matrix-0": {
    "app": "[PATH]/app-debug.apk",
    "test": "[PATH]/debug-1.apk",
    "shards": {
      "shard-0": [
        "class com.TestClassA#test1",
        "class com.TestClassA#test2"
      ],
      "shard-1": [
        "class com.TestClassA#test3",
        "class com.package2.TestClassB#test1",
        "class com.package2.TestClassB#test2"
      ],
      "shard-2": [
        "class com.package2.TestClassB#test4",
        "class com.package2.TestClassB#test3"
      ]
    },
    "junit-ignored": [
      "class com.TestClassA#ignoredTest"
    ]
  },
  "matrix-1": {},
  "matrix-2": {}
}
```

2. for `debug-2.apk` we know that parameterized test takes lots of time so we want to have it in a separate shard:

```json
{
  "matrix-0": {},
  "matrix-1": {
    "app": "[PATH]/app-debug.apk",
    "test": "[PATH]/debug-2.apk",
    "shards": {
      "shard-0": [
        "class com.ParameterizedTest"
      ],
      "shard-1": [
        "class com.package3.TestClass3#test1",
        "class com.package3.TestClass3#test2",
        "class com.package3.TestClass3#test3",
        "class com.package3.TestClass3#test4",
        "class com.package3.TestClass3#test5"
      ]
    },
    "junit-ignored": [
      "class com.package.3.TestClassA#ignoredTest1",
      "class com.package.3.TestClassA#ignoredTest2"
    ]
  },
  "matrix-2": {}
}
```

3. for `debug-3.apk` all tests are rather quick, so we don't care about sharding, let's move them into one shard:

```json
{
  "matrix-0": {},
  "matrix-1": {},
  "matrix-2": {
    "app": "[PATH]/app-debug.apk",
    "test": "gs://path/to/your/bucket/debug-3.apk",
    "shards": {
      "shard-0": [
        "class com.package4.TestClass4#test1",
        "class com.package4.TestClass4#test2",
        "class com.package4.subpackage.TestClass6#test3",
        "class com.package4.TestClass4#test3",
        "class com.package4.subpackage.TestClass6#test1",
        "class com.package4.subpackage.TestClass6#test2"
      ]
    },
    "junit-ignored": [
    ]
  }
}
```

4. Let's save newly created JSON as `custom_sharding.json`

### Add custom sharding to your configuration

Update `flank.yml` with `custom-sharding-json` option:

```yml
gcloud:
  app: ./app-debug.apk
  robo-script: ./MainActivity_robo_script.json
flank:
  max-test-shards: 2
  additional-app-test-apks:
    - test: ./debug-1.apk
    - test: ../build-dir/debug-2.apk
    - test: gs://path/to/your/bucket/debug-3.apk
  custom-sharding-json: ./custom_sharding.json
```

You can verify if shards are correctly applied `flank firebase test android run -c=flank.yml --dump-shards` and check if
that is what you expected

### Start Test Run

Now you can start a flank test run, with updated config there will be still 4 matrices:

* 1x Robo test
* 3x instrumentation tests:
  * `debug-1.apk` with 3 shards
  * `debug-2.apk` with 2 shards
  * `debug-3.apk` with 1 shard

## NOTE:

* flank **DOES NOT** validate provided custom sharding JSON --  it's your responsibility to provide proper configuration
* flank apply sharding by searching test pairs by app apk and test apk paths
* custom sharding supports `gs://` paths
* custom sharding JSON is a source of truth -- no smart sharding is applied (or sharding related configurations)
* matrices ids and shards ids are not important, the only requirement is -- they should be unique
* you can provide custom sharding JSON created entirely from scratch
* custom sharding is very similar to `test-targets-for-shard`, which means you can use the same test targets when
  preparing custom sharding. Below example will create 3 shards, one for each of the packages (`bar`, `foo`, `parameterized`):

    ```json
    {
      "matrix-0": {
        "app": "./any-app.apk",
        "test": "./any-debug.apk",
        "shards": {
          "shard-0": [
            "package com.bar"
          ],
          "shard-1": [
            "package com.parametrized"
          ],
          "shard-2": [
            "package com.similar"
          ]
        },
        "junit-ignored": [
        ]
      }
    }
    ```


## Problems? Something missing?

If you believe there is a problem with the custom sharding, or you would like to have some additional feature -- let us know and create an issue in flank's backlog.
Any feedback is more than welcome!
