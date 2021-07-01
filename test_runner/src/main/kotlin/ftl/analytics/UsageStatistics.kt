package ftl.analytics

import ftl.args.IArgs
import org.json.JSONObject

private const val PROJECT_ID = "project_id"
private const val NAME_KEY = "name"

internal fun IArgs.registerUser(): IArgs = apply {
    messageBuilder.set(
        project, mapOf(PROJECT_ID to project, NAME_KEY to project).toJSONObject()
    ).send()
}

internal fun Map<*, *>.toJSONObject() = JSONObject(this)
