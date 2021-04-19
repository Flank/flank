package ftl.adapter.google

import com.google.testing.model.DeviceIpBlock
import ftl.api.IpBlock
import ftl.client.google.prettyDate

internal fun List<DeviceIpBlock>.toApiModel(): List<IpBlock> = map { deviceIpBlock ->
    IpBlock(
        block = deviceIpBlock.block ?: UNABLE,
        form = deviceIpBlock.form ?: UNABLE,
        addedDate = deviceIpBlock.addedDate?.prettyDate() ?: UNABLE
    )
}

private const val UNABLE = "[Unable to fetch]"
