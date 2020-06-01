package ftl.args.yml

import ftl.args.ArgsHelper.yamlMapper
import ftl.config.Config
import ftl.config.android.AndroidFlankConfig
import ftl.config.android.AndroidGcloudConfig
import ftl.config.common.CommonFlankConfig
import ftl.config.common.CommonGcloudConfig
import ftl.config.ios.IosFlankConfig
import ftl.config.ios.IosGcloudConfig
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import picocli.CommandLine
import kotlin.reflect.KClass
import kotlin.reflect.full.memberProperties

@RunWith(Parameterized::class)
class ConfigTest(
    param: Parameter<Config>
) {
    companion object {
        @JvmStatic
        @Parameterized.Parameters
        fun parameters() = listOf(
            param(
                new = ::CommonGcloudConfig,
                default = { CommonGcloudConfig.default() }
            ),
            param(
                new = ::CommonFlankConfig,
                default = { CommonFlankConfig.default() }
            ),
            param(
                new = ::AndroidGcloudConfig,
                default = { AndroidGcloudConfig.default() }
            ),
            param(
                new = ::AndroidFlankConfig,
                default = { AndroidFlankConfig.default() }
            ),
            param(
                new = ::IosGcloudConfig,
                default = { IosGcloudConfig.default() }
            ),
            param(
                new = ::IosFlankConfig,
                default = { IosFlankConfig.default() }
            )
        )
    }

    val default = param.default
    val new = param.new
    val type = param.type

    @Test
    fun `validate all default values are set explicit`() {
        assertEquals(
            "Pls declare default values for all members explicit",

            type.memberProperties
                .map { it.name }
                .minus("data")
                .sorted(),

            default().data.keys
                .sorted()
        )
    }

    @Test
    fun `load empty config from empty data`() {
        assertEquals(
            new(),
            yamlMapper.readValue("unknown-key: ", type.java)
        )
    }

    @Test
    fun `load empty config from empty cli`() {
        assertEquals(
            new(),
            new().also { CommandLine(it).parseArgs() }
        )
    }

    @Test
    fun `reparse config`() {
        assertEquals(
            default(),
            yamlMapper.run {
                readValue(
                    writeValueAsString(default()),
                    type.java
                )
            }
        )
    }
}

inline fun <reified T : Config> param(
    noinline new: () -> T,
    noinline default: () -> T
) = Parameter(
    new = new,
    default = default,
    type = T::class
)

data class Parameter<T : Config>(
    val new: () -> T,
    val default: () -> T,
    val type: KClass<T>
)
