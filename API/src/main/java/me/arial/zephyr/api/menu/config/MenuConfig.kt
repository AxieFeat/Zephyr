package me.arial.zephyr.api.menu.config

import me.arial.zephyr.api.menu.item.BasicItem

/**
 * Класс для создания конфигураций меню
 */
abstract class MenuConfig {
    /**
     * Название меню
     */
    abstract val title: String

    /**
     * Предметы меню
     */
    abstract val items: MutableMap<String, BasicItem>

    /**
     * Паттерн меню
     */
    abstract val pattern: MutableList<String>

    /**
     * Перезагрузка конфига
     */
    abstract fun reload()
}