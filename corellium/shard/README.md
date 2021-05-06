# Sharding

Depending on the provided test cases duration, the sharding algorithm will:

* Split the test cases from one test apk to run on many devices.
* Group the test cases from many test apks to run on a single device.
* A mix both of the above options.

## Diagram

![sharding_class_diagram](http://www.plantuml.com/plantuml/proxy?cache=no&fmt=svg&src=https://raw.githubusercontent.com/Flank/flank/master/docs/corellium/sharding-class.puml)

## Example

### Input

```yaml
bundle:
  - app: app1
    tests:
      - test: app1-test1
        cases:
          - "class app1.test1.TestClass#test1" // 1s
  - app: app2
    tests:
      - test: app2-test1
        cases:
          - "class app2.test1.TestClass#test2" // 2s
          - "class app2.test1.TestClass#test3" // 3s
      - test: app2-test2
        cases:
          - "class app2.test2.TestClass#test7" // 7s
          - "class app2.test2.TestClass#test8" // 8s
          - "class app2.test2.TestClass#test9" // 9s
```

### Output

#### Max shards 3

```yaml
shards:
  - shard1:
      - app: app1
        tests:
          - test: app1-test1
            cases:
              - "class app1.test1.TestClass#test1" // 1s
      - app: app2
        tests:
          - test: app2-test2
            cases:
              - "class app2.test2.TestClass#test9" // 9s
  - shard2:
      - app: app2
        test: app2-test1
        cases:
          - "class app1.test2.TestClass#test2" // 2s
      - app: app2
        test: app2-test2
        cases:
          - "class app2.test2.TestClass#test8" // 8s
  - shard3:
      - app: app2
        test: app2-test1
        cases:
          - "class app2.test1.TestClass#test3" // 3s
      - app: app2
        test: app2-test2
        cases:
          - "class app2.test2.TestClass#test7" // 7s
```

#### Max shards 2

```yaml
shards:
  - shard1:
      - app: app1
        tests:
          - test: app1-test1
            cases:
              - "class app1.test1.TestClass#test1" // 1sz
      - app: app2
        tests:
          - test: app2-test1
            cases:
              - "class app2.test1.TestClass#test2" // 2s
              - "class app2.test2.TestClass#test3" // 3s
          - test: app2-test2
            cases:
              - "class app2.test2.TestClass#test9" // 9s
  - shard2:
      - app: app2
        tests:
          - test: app2-test2
            cases:
              - "class app2.test2.TestClass#test7" // 7s
              - "class app2.test2.TestClass#test8" // 8s
```
