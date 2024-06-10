package me.arial.zephyr.core.config

import me.arial.zephyr.api.configuration.Config
import me.arial.zephyr.api.debug.DebugLevel
import me.arial.zephyr.api.debug.debugLevel
import me.arial.zephyr.core.Zephyr

class SettingsConfig : Config(Zephyr.instance, "settings.yml", fromJar = true) {

    override fun checkDefault() {
        setIfNotExists("player-message-receive-event", false)
        setIfNotExists("debug-level", DebugLevel.NONE.name)

        debugLevel = DebugLevel.valueOf(config.getString("debug-level") ?: "NONE")
    }

    val playerMessageReceiveEventEnabled: Boolean
        get() = config.getBoolean("player-message-receive-event")

}