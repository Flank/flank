package flank.corellium.domain.test.android.task

import flank.corellium.domain.TestAndroid.DeviceCostPerSecond
import flank.exection.parallel.using

// TODO fetch this value from corellium API.
val fetchDeviceCostPerSecond = DeviceCostPerSecond using { 0L }
