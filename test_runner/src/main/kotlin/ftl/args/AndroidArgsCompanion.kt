package ftl.args

import com.google.common.annotations.VisibleForTesting
import ftl.cli.firebase.test.android.AndroidRunCommand
import ftl.config.android.AndroidFlankConfig
import ftl.config.android.AndroidGcloudConfig
import ftl.config.common.CommonFlankConfig
import ftl.config.common.CommonGcloudConfig
import ftl.config.defaultAndroidConfig
import ftl.config.loadAndroidConfig
import ftl.config.plus
import ftl.util.loadFile
import java.io.Reader
import java.nio.file.Path

open class AndroidArgsCompanion : IArgs.ICompanion {
    override val validArgs by lazy {
        ArgsHelper.mergeYmlMaps(
            CommonGcloudConfig,
            AndroidGcloudConfig,
            CommonFlankConfig,
            AndroidFlankConfig
        )
    }

    fun default() =
        createAndroidArgs(defaultAndroidConfig())

    fun load(yamlPath: Path, cli: AndroidRunCommand? = null) =
        load(loadFile(yamlPath), cli)

    @VisibleForTesting
    internal fun load(yamlReader: Reader, cli: AndroidRunCommand? = null) =
        createAndroidArgs(
            config = defaultAndroidConfig() +
                    loadAndroidConfig(reader = yamlReader) +
                    cli?.config
        ).apply {
            commonArgs.validate()
            this.validate()
        }
}
