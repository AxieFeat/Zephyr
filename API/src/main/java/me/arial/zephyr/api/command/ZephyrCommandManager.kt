package me.arial.zephyr.api.command

import me.arial.zephyr.api.module.command.ZephyrModuleCommandManager;

/**
 * Класс для работы с командами Zephyr
 *
 * Для работы из модулей используйте [ZephyrModuleCommandManager]
 */
interface ZephyrCommandManager {

    /**
     * Мапа всех команд
     */
    val registeredCommands: MutableMap<CommandInfo, Any>

    /**
     * Регистрирует команду
     *
     * @param obj Объект команды, который наследует [ZephyrCommand]
     * @return true, если команда зарегистрирована, и false, если нет
     */
    fun registerCommand(obj: Any): Boolean


    /**
     * Отменяет регистрацию для команды
     *
     * @param obj Объект команды, который необходимо разрегистрировать
     */
    fun unregisterCommand(obj: Any)
}