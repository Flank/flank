# Mixpanel events in Flank

## Document version: 1.0

Flank is currently tracking event with following properties:

- schema_version
- flank_version
- project_id
- app_id
- device_types
- cost 
    - virtual
    - physical
    - total
- shard_count
- tests
  - total
  - successful
  - failed
  - flaky
- test_duration 
    - total
    - physical
    - virtual
- outcome 
- configuration

Every event contains a session id and project id. By ```project id``` you can find all events from every execution with specific ```project id```. By ```session id``` you can find events from specific Flank execution.

## Schema version [schema_version]

This property contains version of schema, this property should be equals to this document version.

## Flank Version [flank_version]

By these event's we can check how frequently users upgrade the Flank version.

## Project id [project_id]

This property contains id of project used in firebase.

## Bundle and package id [app_id]

This event contains information about the bundle id for ios and the package id for android project. Additionally,
contains platform type (android, ios). This event allows us to implement a ```Who uses Flank``` report with additional breakdown by ```device type```.

## Device types [device_types]

This property contains information about used devices types.

## Shards count [shard_count]

This property contains information about count of shards.

## Tests result information [test]

This property contains information about count of tests and count of successful, failed and flaky tests.

Fields:

- ```total```
- ```successful```
- ```failed```
- ```flaky```

## Tests duration [test_duration]

This property contains information about the time of execution of tests by device type (virtual, physical, total)

Fields:

- ```virtual```
- ```physical```
- ```total```

## Outcome [outcome]

This property contains information about the outcome of the entire run. More information you can find here: https://github.com/Flank/flank/blob/3a213579b0b8ed7ca018314f0619146408292b35/docs/feature/summary_output.md#possible-outputs

## Total cost and cost per device type [cost]

With these event's we can realize the report ```How many millions per month is spent on Firebase Test lab by way of using Flank? ```

Fields:

- ```virtual```
- ```physical```
- ```total```

## Configuration [configuration]

This event contains information about the configuration executed in Flank. This event allows us to
track information about devices and features used in Flank.
It could be useful to check what features are most important for the community.

Fields reflect configuration names.
