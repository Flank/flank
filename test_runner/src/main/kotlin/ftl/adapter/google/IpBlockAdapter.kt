package ftl.adapter.google

import com.google.testing.model.DeviceIpBlock
import ftl.api.IpBlockList
import ftl.client.google.prettyDate

internal fun List<DeviceIpBlock>.toApiModel(): IpBlockList = IpBlockList(
    map { deviceIpBlock ->
        IpBlockList.IpBlock(
            block = deviceIpBlock.block ?: UNABLE,
            form = deviceIpBlock.form ?: UNABLE,
            addedDate = deviceIpBlock.addedDate?.prettyDate() ?: UNABLE
        )
    }
)

private const val UNABLE = "[Unable to fetch]"
