package me.arial.zephyr.api.module

import net.kyori.adventure.text.minimessage.MiniMessage
import java.util.logging.LogManager
import java.util.logging.Logger

/**
 * Класс логгера для модуля Zephyr
 *
 * Сообщения выводятся с префиксом Zephyr-МОДУЛЬ
 *
 * @param name Название модуля
 */
class ZephyrLogger private constructor(
    name: String
) : Logger("Zephyr-$name", null) {

    companion object {
        fun getLogger(name: String): Logger {
            val logger = ZephyrLogger(name)

            LogManager.getLogManager().addLogger(logger)

            return LogManager.getLogManager().getLogger("Zephyr-$name")
        }
    }

}