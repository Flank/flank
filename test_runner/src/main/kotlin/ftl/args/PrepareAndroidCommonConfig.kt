package ftl.args

import ftl.client.google.AndroidCatalog
import ftl.config.AndroidConfig
import ftl.config.Device
import ftl.config.containsPhysicalDevices
import ftl.config.containsVirtualDevices

fun AndroidConfig.prepareAndroidCommonConfig() = common.createCommonArgs(data).let { commonArgs ->
    commonArgs.devices.resolveDeviceType(commonArgs.project).updateMaxTestShards(commonArgs)
}

private fun List<Device>.resolveDeviceType(projectId: String) = map { device ->
    device.copy(isVirtual = AndroidCatalog.isVirtualDevice(device.model, projectId))
}

private fun List<Device>.updateMaxTestShards(commonArgs: CommonArgs) =
    commonArgs.copy(devices = this, maxTestShards = this.calculateMaxTestShards(commonArgs.maxTestShards))

private fun List<Device>.calculateMaxTestShards(maxTestShards: Int) =
    if (maxTestShards == -1) getMaxShardsByDevice()
    else scaleMaxShardsByDevice(maxTestShards)

private fun List<Device>.getMaxShardsByDevice() =
    if (containsPhysicalDevices()) IArgs.AVAILABLE_PHYSICAL_SHARD_COUNT_RANGE.last
    else IArgs.AVAILABLE_VIRTUAL_SHARD_COUNT_RANGE.last

private fun List<Device>.scaleMaxShardsByDevice(maxTestShards: Int) =
    if (containsPhysicalDevices() && containsVirtualDevices()) maxTestShards.scaleToPhysicalShardsCount()
    else maxTestShards

private fun Int.scaleToPhysicalShardsCount() = if (this !in IArgs.AVAILABLE_PHYSICAL_SHARD_COUNT_RANGE)
    IArgs.AVAILABLE_PHYSICAL_SHARD_COUNT_RANGE.last else this
