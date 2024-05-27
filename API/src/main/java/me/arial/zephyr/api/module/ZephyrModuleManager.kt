package me.arial.zephyr.api.module

import java.io.File
import me.arial.zephyr.api.event.module.*

/**
 * Класс для работы с модулями Zephyr
 */
interface ZephyrModuleManager {

    /**
     * Папка для модулей
     */
    val moduleFolder: File

    /**
     * Загрузить модуль из файла
     *
     * @param file Файл модуля
     * @param withEvent Должен ли срабатывать ивент [ModuleLoadEvent]
     *
     * @return Объект [LoadedModule], null если не удалось загрузить
     */
    fun loadModule(file: File, withEvent: Boolean = false): LoadedModule?

    /**
     * Загрузить модули из папки с модулями
     *
     * @return true, если успешная загрузка, иначе false
     */
    fun loadModules(): Boolean

    /**
     * @return Мапа всех загруженных модулей
     */
    fun loadedModules(): Map<String, LoadedModule>

    /**
     * Выгружает модуль
     *
     * @param loadedModule Модуль для выгрузки
     * @param withEvent Должен ли срабатывать ивент [ModuleUnloadEvent]
     *
     * @return true, если успешная выгрузка, иначе false
     */
    fun unloadModule(loadedModule: LoadedModule?, withEvent: Boolean = false): Boolean

    /**
     * Выгружает модуль
     *
     * @param module Модуль для выгрузки
     * @param withEvent Должен ли срабатывать ивент [ModuleUnloadEvent]
     *
     * @return true, если успешная выгрузка, иначе false
     */
    fun unloadModule(module: ZephyrModule, withEvent: Boolean = false): Boolean

    /**
     * Перезагружает модуль
     *
     * @param loadedModule Модуль для перезагрузки
     * @param withEvent Должен ли срабатывать ивент [ModuleReloadEvent]
     *
     * @return true, если успешная перезагрузка, иначе false
     */
    fun reloadModule(loadedModule: LoadedModule, withEvent: Boolean = false): Boolean

    /**
     * Перезагружает модуль
     *
     * @param module Модуль для перезагрузки
     * @param withEvent Должен ли срабатывать ивент [ModuleReloadEvent]
     *
     * @return true, если успешная перезагрузка, иначе false
     */
    fun reloadModule(module: ZephyrModule, withEvent: Boolean = false): Boolean

     /**
     * Получить модуль по его названию
     *
     * @param name Название модуля
     *
     * @return Объект [LoadedModule], null если не найден
     */
    fun getModule(name: String?): LoadedModule?
}