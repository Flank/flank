package ftl.util

import com.bugsnag.Bugsnag
import java.nio.file.Paths

internal object BugsnagInitHelper {

    private const val GSUTIL_FOLDER = ".gsutil"
    private const val ANALYTICS_FILE = "analytics-uuid"
    private const val DISABLED = "DISABLED"
    private const val FLANK_API_KEY = "3d5f8ba4ee847d6bb51cb9c347eda74f"

    internal fun initBugsnag(useMock: Boolean) =
        Paths.get(System.getProperty("user.home"), "$GSUTIL_FOLDER/$ANALYTICS_FILE").toFile().run {
            if (useMock) null
            else if (exists() && readText().trim() == DISABLED) null
            else Bugsnag(FLANK_API_KEY)
        }
}
