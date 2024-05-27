package me.arial.zephyr.api.module

/**
 * Объект загруженного модуля
 *
 * @param module Главный класс модуля, который наследует [ZephyrModule]
 * @param name Название модуля
 * @param version Версия модуля
 * @param reloadable Перезагружаемость модуля
 * @param authors Авторы модуля
 * @param description Описание модуля
 * @param website Сайт модуля
 * @param depend Обязательные зависимости модуля
 * @param soft Необязательные зависимости модуля
 */
data class LoadedModule(
    val module: ZephyrModule,
    val name: String,
    val version: String,
    val reloadable: Boolean,
    val authors: Array<String>,
    val description: String,
    val website: String,
    val depend: Array<String>,
    val soft: Array<String>
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as LoadedModule

        if (module != other.module) return false
        if (name != other.name) return false
        if (version != other.version) return false
        if (reloadable != other.reloadable) return false
        if (!authors.contentEquals(other.authors)) return false
        if (description != other.description) return false
        if (website != other.website) return false
        if (!depend.contentEquals(other.depend)) return false
        if (!soft.contentEquals(other.soft)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = module.hashCode()
        result = 31 * result + name.hashCode()
        result = 31 * result + version.hashCode()
        result = 31 * result + reloadable.hashCode()
        result = 31 * result + authors.contentHashCode()
        result = 31 * result + description.hashCode()
        result = 31 * result + website.hashCode()
        result = 31 * result + depend.contentHashCode()
        result = 31 * result + soft.contentHashCode()
        return result
    }

}