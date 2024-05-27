package me.arial.zephyr.api.command

/**
 * Аннотация для создания команды
 *
 * @param name Название команды
 * @param description Описание команды
 * @param aliases Алиасы команды
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class CommandInfo(
    val name: String,
    val description: String = "Не указано",
    val aliases: Array<String> = []
)
