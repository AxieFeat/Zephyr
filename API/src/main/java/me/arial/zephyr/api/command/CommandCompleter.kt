package me.arial.zephyr.api.command

/**
 * Аннотация для обозначения стандартного комплитера из Bukkit
 *
 * @param command На какие команды будет реагировать аннотированная функция
 * @param advancedArgs Будут ли объединяться аргументы, которые заключены в " или ' в один аргумент
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class CommandCompleter(
    val command: Array<String>,
    val advancedArgs: Boolean = false
)
