package net.starliteheart.cobblemon_iwa.common

import me.shedaniel.autoconfig.AutoConfig
import me.shedaniel.autoconfig.serializer.Toml4jConfigSerializer
import net.starliteheart.cobblemon_iwa.common.config.IWAConfig
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

object CobblemonIWA {
    const val MOD_ID = "cobblemon_iwa"
    var LOGGER: Logger = LogManager.getLogger()

    fun initialize() {
        AutoConfig.register(IWAConfig::class.java, ::Toml4jConfigSerializer)
    }
}