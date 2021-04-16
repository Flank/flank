package flank.corellium.adapter

import flank.corellium.api.AndroidInstance
import flank.corellium.api.AndroidInstance.Config

object InvokeAndroidDevices : AndroidInstance.Invoke {
    override suspend fun Config.invoke(): List<String> = TODO()
}
