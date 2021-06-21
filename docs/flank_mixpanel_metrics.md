# Mixpanel events in Flank

Flank currently tracking the following events:

- configuration
- bundle id and package name with information about the device type
- total cost
- cost per device type
- test duration
- flank version

Every event contains a session id and project id. By ```project id``` you can find all events from every execution with specific ```project id```. By ```session id``` you can find events from specific
Flank execution.

## Configuration

This event contains information about configuration executed in Flank. This event allows us
track information about devices and features used in Flank.
It could be useful to check what features are most important for the community.

Fields reflect configuration names.

## Bundle and package id [app_id]

This event contains information about bundle id for ios and package id for android project. Additionally,
contains platform type (android, ios). This event allows implementing ```Who uses Flank``` report with additional breakdown by ```device type```.

Fields:

- ```app_id```
- ```device_type```

## Total cost and cost per device type [devices_cost]

By this event's we can implement the report ```How many millions per month in spend is Flank responsible for on Firebase Test Lab? ```

Fields:

- ```virtual_cost```
- ```physical_cost```
- ```total_cost```

## Test duration [total_test_time]

By this event's we can check how long tests took.

Fields:

- ```test_duration```

## Flank Version [flank_version]

By this event's we can check how frequently users upgrade the Flank version.

Fields:

- ```version```
