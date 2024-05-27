package me.arial.zephyr.api.module.command

import me.arial.zephyr.api.command.ZephyrCommandManager

/**
 * Класс для работы с командами Zephyr из модулей
 */
interface ZephyrModuleCommandManager : ZephyrCommandManager {

    /**
     * Отменить регистрацию для всех команд модуля
     */
    fun unregisterCommands()
}