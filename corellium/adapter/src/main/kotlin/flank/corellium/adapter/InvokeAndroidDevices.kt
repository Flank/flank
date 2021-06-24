package flank.corellium.adapter

import flank.corellium.api.AndroidInstance
import flank.corellium.api.AndroidInstance.Event
import flank.corellium.client.core.createNewInstance
import flank.corellium.client.core.getAllProjects
import flank.corellium.client.core.getProjectInstancesList
import flank.corellium.client.core.startInstance
import flank.corellium.client.core.waitUntilInstanceIsReady
import flank.corellium.client.data.Instance
import flank.corellium.client.data.Instance.BootOptions.AdditionalTags.GPU
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch

private const val FLANK_INSTANCE_NAME_PREFIX = "flank-android-"
private const val FLAVOUR = "ranchu"
private const val OS = "11.0.0"
private const val SCREEN = "720x1280:280"

fun invokeAndroidDevices(
    projectName: String,
) = AndroidInstance.Invoke { config ->
    channelFlow<Event> {
        val projectId = getProjectId(projectName)
        val instances = getCreatedInstances(projectId, config.amount)
        startNotRunningInstances(instances)

        val ids = instances.map(Instance::id) + let {
            // When existing instances size match required amount
            // there is not needs for creating more instances.
            if (instances.size == config.amount) emptyList()
            // Otherwise is required to create some additional instances
            else createInstances(
                projectId = projectId,
                gpuAcceleration = config.gpuAcceleration,
                indexes = calculateAdditionalIndexes(
                    current = instances,
                    requiredAmount = config.amount
                ),
            )
        }

        waitForInstances(ids)
    }
}

// Important!!!
// Try to keep the private methods not dependent on each other.
// Otherwise the implementation will go complicated.

private typealias OutputChannel = SendChannel<Event>

/**
 * @return The project id for given project name.
 */
private suspend fun getProjectId(name: String) =
    corellium.getAllProjects().first { it.name == name }.id

/**
 * Get all instances that was already created for flank.
 * @return [List] of [Instance] where [List.size] <= [amount]
 */
private suspend fun OutputChannel.getCreatedInstances(
    projectId: String,
    amount: Int
): List<Instance> = corellium
    .also { send(Event.GettingAlreadyCreated) }
    .getProjectInstancesList(projectId)
    .filter { it.name.startsWith(FLANK_INSTANCE_NAME_PREFIX) }
    .filter { it.state !in Instance.State.unavailable }
    .take(amount)
    .apply { send(Event.Obtained(size)) }

/**
 * Start all given instances with status different than "on".
 */
private suspend fun OutputChannel.startNotRunningInstances(
    instances: List<Instance>
): Unit = instances
    .filter { it.state != Instance.State.ON }
    .apply { if (isNotEmpty()) send(Event.Starting(size)) }
    .forEach { instance ->
        corellium.startInstance(instance.id)
        send(Event.Started(instance.id, instance.name))
    }

/**
 * Create new instances basing on given indexes.
 */
private suspend fun OutputChannel.createInstances(
    projectId: String,
    indexes: List<Int>,
    gpuAcceleration: Boolean,
) = indexes
    .apply { send(Event.Creating(size)) }
    .map { index ->
        corellium.createNewInstance(
            Instance(
                project = projectId,
                name = FLANK_INSTANCE_NAME_PREFIX + index,
                flavor = FLAVOUR,
                os = OS,
                bootOptions = Instance.BootOptions(
                    screen = SCREEN,
                    additionalTags = listOfNotNull(
                        GPU.takeIf { gpuAcceleration }
                    )
                )
            )
        )
    }

/**
 * Calculate the list indexes for additional instances to create.
 */
private fun calculateAdditionalIndexes(
    current: List<Instance>,
    requiredAmount: Int,
): List<Int> =
    (0 until requiredAmount).toSet() // Create set of possible indexes starting from 0
        .minus(current.map(Instance::index)) // Subtract already created indexes
        .sorted()
        .take(requiredAmount - current.size) // Skip the excess indexes

/**
 * The index encoded in instance name.
 * This will work only on devices created by flank.
 */
private val Instance.index get() = name.removePrefix(FLANK_INSTANCE_NAME_PREFIX).toInt()

/**
 * Block the execution and wait until each instance change the status to "on".
 */
private suspend fun OutputChannel.waitForInstances(
    ids: List<String>
) = coroutineScope {
    send(Event.Waiting)
    ids.map { id ->
        launch {
            corellium.waitUntilInstanceIsReady(id)
            send(Event.Ready(id))
        }
    }.joinAll()
    send(Event.AllReady)
}
