package me.arial.zephyr.api.command

/**
 * Аннотация для обозначения стандартного исполнителя команды из Bukkit
 *
 * @param command На какие команды будет реагировать аннотированная функция
 * @param color Должны ли парсится цвета в возващаемом результате командой (Только для String)
 * @param advancedArgs Будут ли объединяться аргументы, которые заключены в " или ' в один аргумент
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class CommandExecutor(
    val command: Array<String>,
    val color: Boolean = false,
    val advancedArgs: Boolean = false
)
