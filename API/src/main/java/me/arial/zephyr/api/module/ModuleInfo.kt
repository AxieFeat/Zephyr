package me.arial.zephyr.api.module

/**
 * Аннотация с информацией о модуле
 *
 * @param name Название модуля
 * @param version Версия модуля
 * @param reloadable Перезагружаемость модуля
 * @param authors Авторы модуля
 * @param description Описание модуля
 * @param website Сайт модуля
 * @param depend Обязательные зависимости модуля
 * @param soft Необязательные зависимости модуля
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class ModuleInfo(
    val name: String,
    val version: String,
    val reloadable: Boolean,
    val authors: Array<String> = [],
    val description: String = "Не указано",
    val website: String = "Не указано",
    val depend: Array<String> = [],
    val soft: Array<String> = []
)

