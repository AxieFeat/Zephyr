package me.arial.zephyr.api.command.commodore

/**
 * Аннотация для обозначения параметра от библиотеки [me.lucko.commodore]
 *
 * @param permission Пермишен, которой должен быть у игрока, чтобы команда была доступна
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class CommodorePermission(
    val permission: String
)