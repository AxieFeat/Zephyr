package me.arial.zephyr.api.command.brigadier

/**
 * Аннотация для обозначения параметра от библиотеки [dev.jorel.commandapi]
 *
 * @param permission Пермишен, которого у игрока НЕ должно быть, чтобы команда была доступна
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class WithoutPermission(
    val permission: String
)