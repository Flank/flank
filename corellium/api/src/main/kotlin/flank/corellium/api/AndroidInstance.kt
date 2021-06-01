package flank.corellium.api

import kotlinx.coroutines.flow.Flow

/**
 * The scope is representing API for managing the corellium android instances.
 *
 * [AndroidInstance] is abstract representation of virtual android device.
 */
object AndroidInstance {

    /**
     * Configuration for devices to invoke.
     *
     * @property amount The amount of devices to invoke.
     * @property gpuAcceleration Enables gpu acceleration for virtual devices.
     */
    data class Config(
        val amount: Int,
        val gpuAcceleration: Boolean
    )

    /**
     * Invoke the android corellium devices.
     *
     * After successful invoke, the devices specified in th [Config] should be running and ready for use.
     *
     * @return List of invoked device ids.
     */
    fun interface Invoke : (Config) -> Flow<String>
}
