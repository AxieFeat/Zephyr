package me.arial.zephyr.api.command.brigadier

/**
 * Аннотация для обозначения параметра от библиотеки [dev.jorel.commandapi]
 *
 * @param permission Пермишен, которой должен быть у игрока, чтобы команда была доступна
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class WithPermission(
    val permission: String
)
