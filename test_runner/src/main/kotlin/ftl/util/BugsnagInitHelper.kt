package ftl.util

import com.bugsnag.Bugsnag
import java.io.File

internal object BugsnagInitHelper {

    private const val GSUTIL_FOLDER = ".gsutil"
    private const val ANALYTICS_FILE = "analytics-uuid"
    private const val DISABLED = "DISABLED"
    private const val FLANK_API_KEY = "3d5f8ba4ee847d6bb51cb9c347eda74f"

    internal fun initBugsnag(
        useMock: Boolean,
        rootPath: String = System.getProperty("user.home")
    ) = when {
            useMock -> null
            analyticsFileExistAndIsDisabled(rootPath) -> null
            else -> Bugsnag(FLANK_API_KEY)
        }

    private fun analyticsFileExistAndIsDisabled(rootPath: String) =
        File(rootPath, "$GSUTIL_FOLDER/$ANALYTICS_FILE").run { exists() && readText().trim() == DISABLED }
}
