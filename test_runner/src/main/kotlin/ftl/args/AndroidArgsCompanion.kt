package ftl.args

import com.google.common.annotations.VisibleForTesting
import ftl.args.yml.AndroidFlankYml
import ftl.args.yml.AndroidGcloudYml
import ftl.args.yml.FlankYml
import ftl.args.yml.GcloudYml
import ftl.cli.firebase.test.android.AndroidRunCommand
import ftl.config.defaultAndroidConfig
import ftl.config.loadAndroidConfig
import ftl.config.plus
import ftl.util.loadFile
import java.io.Reader
import java.nio.file.Path

open class AndroidArgsCompanion : IArgs.ICompanion {
    override val validArgs by lazy {
        ArgsHelper.mergeYmlMaps(
            GcloudYml,
            AndroidGcloudYml,
            FlankYml,
            AndroidFlankYml
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
