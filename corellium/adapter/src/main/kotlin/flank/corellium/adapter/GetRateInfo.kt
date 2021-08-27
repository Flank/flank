package flank.corellium.adapter

import flank.corellium.api.AndroidInstance
import flank.corellium.api.InstanceId
import flank.corellium.client.core.rate
import kotlinx.coroutines.async

fun getRateInfo(
    corellium: GetCorellium
) = AndroidInstance.GetRate { id: InstanceId ->
    val api = corellium()
    api.async {
        val rate = api.rate(id)
        AndroidInstance.RateInfo(
            onRateMicroCents = rate.onRateMicrocents,
            offRateMicroCents = rate.offRateMicrocents,
        )
    }
}
