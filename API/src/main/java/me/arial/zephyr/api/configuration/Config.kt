package me.arial.zephyr.api.configuration

import me.arial.zephyr.api.getLangComponent
import me.arial.zephyr.api.module.ZephyrModule
import me.arial.zephyr.api.text.LangComponent
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.plugin.java.JavaPlugin
import java.io.File
import java.io.IOException

/**
 * Класс для работы с файлами конфигурации
 */
abstract class Config private constructor(
    folder: File?,
    name: String
) {

    companion object {
        val miniMessage = MiniMessage.builder().build()
    }

    /**
     * Файл конфигурации
     */
    private val rawConfig = File(folder, name)

    /**
     * Объект конфигурации
     */
    protected lateinit var config: YamlConfiguration

    /**
     * Мапа всех [LangComponent] из конфига
     */
    private val langComponents: MutableMap<String, LangComponent> = mutableMapOf()

    @Deprecated("use constructor without 'parseColor'")
    constructor(
        zephyrModule: ZephyrModule,
        name: String,
        fromJar: Boolean = false,
        parseColor: Boolean = true,
        parseLangComponent: Boolean = false
    ): this(zephyrModule.dataFolder, name) {
        var isFirstLoad = false
        if (!rawConfig.exists()) {
            try {
                if (fromJar) {

                    zephyrModule.saveResource(name, false)

                } else {
                    rawConfig.createNewFile()
                }
                isFirstLoad = true
            } catch (e: IOException) {
                throw RuntimeException(e)
            }
        }

        this.config = loadConfiguration(rawConfig)

        if (parseLangComponent) {
            config.getConfigurationSection("")!!.getKeys(true).forEach { key ->
                var finalKey = key

                when {
                    key.endsWith(".message") -> {
                        finalKey = finalKey.replace(".message", "")
                        if (!langComponents.containsKey(finalKey)) {
                            langComponents[finalKey] = config.getLangComponent(finalKey)
                        }
                    }

                    key.endsWith(".sound") -> {
                        finalKey = finalKey.replace(".sound", "")
                        if (!langComponents.containsKey(finalKey)) {
                            langComponents[finalKey] = config.getLangComponent(finalKey)
                        }
                    }

                    key.endsWith(".sound.type") -> {
                        finalKey = finalKey.replace(".sound.type", "")
                        if (!langComponents.containsKey(finalKey)) {
                            langComponents[finalKey] = config.getLangComponent(finalKey)
                        }
                    }

                    key.endsWith(".sound.volume") -> {
                        finalKey = finalKey.replace(".sound.volume", "")
                        if (!langComponents.containsKey(finalKey)) {
                            langComponents[finalKey] = config.getLangComponent(finalKey)
                        }
                    }

                    key.endsWith(".sound.pitch") -> {
                        finalKey = finalKey.replace(".sound.pitch", "")
                        if (!langComponents.containsKey(finalKey)) {
                            langComponents[finalKey] = config.getLangComponent(finalKey)
                        }
                    }

                    else -> {
                        if (config.isList(finalKey) || config.isString(finalKey)) {
                            if (!langComponents.containsKey(finalKey)) {
                                langComponents[finalKey] = config.getLangComponent(finalKey)
                            }
                        }
                    }
                }
            }
        }

        if (isFirstLoad) onFirstLoad()
        checkDefault()
        save()
    }

    constructor(
        zephyrModule: ZephyrModule,
        name: String,
        fromJar: Boolean = false,
        parseLangComponent: Boolean = false
    ): this(zephyrModule.dataFolder, name) {
        var isFirstLoad = false
        if (!rawConfig.exists()) {
            try {
                if (fromJar) {

                    zephyrModule.saveResource(name, false)

                } else {
                    rawConfig.createNewFile()
                }
                isFirstLoad = true
            } catch (e: IOException) {
                throw RuntimeException(e)
            }
        }

        this.config = loadConfiguration(rawConfig)

        if (parseLangComponent) {
            config.getConfigurationSection("")!!.getKeys(true).forEach { key ->
                var finalKey = key

                when {
                    key.endsWith(".message") -> {
                        finalKey = finalKey.replace(".message", "")
                        if (!langComponents.containsKey(finalKey)) {
                            langComponents[finalKey] = config.getLangComponent(finalKey)
                        }
                    }

                    key.endsWith(".sound") -> {
                        finalKey = finalKey.replace(".sound", "")
                        if (!langComponents.containsKey(finalKey)) {
                            langComponents[finalKey] = config.getLangComponent(finalKey)
                        }
                    }

                    key.endsWith(".sound.type") -> {
                        finalKey = finalKey.replace(".sound.type", "")
                        if (!langComponents.containsKey(finalKey)) {
                            langComponents[finalKey] = config.getLangComponent(finalKey)
                        }
                    }

                    key.endsWith(".sound.volume") -> {
                        finalKey = finalKey.replace(".sound.volume", "")
                        if (!langComponents.containsKey(finalKey)) {
                            langComponents[finalKey] = config.getLangComponent(finalKey)
                        }
                    }

                    key.endsWith(".sound.pitch") -> {
                        finalKey = finalKey.replace(".sound.pitch", "")
                        if (!langComponents.containsKey(finalKey)) {
                            langComponents[finalKey] = config.getLangComponent(finalKey)
                        }
                    }

                    else -> {
                        if (config.isList(finalKey) || config.isString(finalKey)) {
                            if (!langComponents.containsKey(finalKey)) {
                                langComponents[finalKey] = config.getLangComponent(finalKey)
                            }
                        }
                    }
                }
            }
        }

        if (isFirstLoad) onFirstLoad()
        checkDefault()
        save()
    }


    @Deprecated("use constructor without 'parseColor'")
    constructor(
        plugin: JavaPlugin,
        name: String,
        fromJar: Boolean = false,
        parseColor: Boolean = true,
        parseLangComponent: Boolean = false
    ): this(plugin.dataFolder, name) {
        var isFirstLoad = false
        if (!rawConfig.exists()) {
            try {
                if (fromJar) {
                    plugin.saveResource(name, false)
                } else {
                    rawConfig.createNewFile()
                }
                isFirstLoad = true
            } catch (e: IOException) {
                throw RuntimeException(e)
            }
        }

        this.config = loadConfiguration(rawConfig)

        if (parseLangComponent) {
            config.getConfigurationSection("")!!.getKeys(true).forEach { key ->
                var finalKey = key

                when {
                    key.endsWith(".message") -> {
                        finalKey = finalKey.replace(".message", "")
                        if (!langComponents.containsKey(finalKey)) {
                            langComponents[finalKey] = config.getLangComponent(finalKey)
                        }
                    }
                    key.endsWith(".sound") -> {
                        finalKey = finalKey.replace(".sound", "")
                        if (!langComponents.containsKey(finalKey)) {
                            langComponents[finalKey] = config.getLangComponent(finalKey)
                        }
                    }
                    key.endsWith(".sound.type") -> {
                        finalKey = finalKey.replace(".sound.type", "")
                        if (!langComponents.containsKey(finalKey)) {
                            langComponents[finalKey] = config.getLangComponent(finalKey)
                        }
                    }
                    key.endsWith(".sound.volume") -> {
                        finalKey = finalKey.replace(".sound.volume", "")
                        if (!langComponents.containsKey(finalKey)) {
                            langComponents[finalKey] = config.getLangComponent(finalKey)
                        }
                    }
                    key.endsWith(".sound.pitch") -> {
                        finalKey = finalKey.replace(".sound.pitch", "")
                        if (!langComponents.containsKey(finalKey)) {
                            langComponents[finalKey] = config.getLangComponent(finalKey)
                        }
                    }
                    else -> {
                        if (config.isList(finalKey) || config.isString(finalKey)) {
                            if (!langComponents.containsKey(finalKey)) {
                                langComponents[finalKey] = config.getLangComponent(finalKey)
                            }
                        }
                    }
                }
            }
        }

        if (isFirstLoad) onFirstLoad()
        checkDefault()
        save()
    }

    constructor(
        plugin: JavaPlugin,
        name: String,
        fromJar: Boolean = false,
        parseLangComponent: Boolean = false
    ): this(plugin.dataFolder, name) {
        var isFirstLoad = false
        if (!rawConfig.exists()) {
            try {
                if (fromJar) {
                    plugin.saveResource(name, false)
                } else {
                    rawConfig.createNewFile()
                }
                isFirstLoad = true
            } catch (e: IOException) {
                throw RuntimeException(e)
            }
        }

        this.config = loadConfiguration(rawConfig)

        if (parseLangComponent) {
            config.getConfigurationSection("")!!.getKeys(true).forEach { key ->
                var finalKey = key

                when {
                    key.endsWith(".message") -> {
                        finalKey = finalKey.replace(".message", "")
                        if (!langComponents.containsKey(finalKey)) {
                            langComponents[finalKey] = config.getLangComponent(finalKey)
                        }
                    }
                    key.endsWith(".sound") -> {
                        finalKey = finalKey.replace(".sound", "")
                        if (!langComponents.containsKey(finalKey)) {
                            langComponents[finalKey] = config.getLangComponent(finalKey)
                        }
                    }
                    key.endsWith(".sound.type") -> {
                        finalKey = finalKey.replace(".sound.type", "")
                        if (!langComponents.containsKey(finalKey)) {
                            langComponents[finalKey] = config.getLangComponent(finalKey)
                        }
                    }
                    key.endsWith(".sound.volume") -> {
                        finalKey = finalKey.replace(".sound.volume", "")
                        if (!langComponents.containsKey(finalKey)) {
                            langComponents[finalKey] = config.getLangComponent(finalKey)
                        }
                    }
                    key.endsWith(".sound.pitch") -> {
                        finalKey = finalKey.replace(".sound.pitch", "")
                        if (!langComponents.containsKey(finalKey)) {
                            langComponents[finalKey] = config.getLangComponent(finalKey)
                        }
                    }
                    else -> {
                        if (config.isList(finalKey) || config.isString(finalKey)) {
                            if (!langComponents.containsKey(finalKey)) {
                                langComponents[finalKey] = config.getLangComponent(finalKey)
                            }
                        }
                    }
                }
            }
        }

        if (isFirstLoad) onFirstLoad()
        checkDefault()
        save()
    }

    /**
     * Получение [LangComponent] из указанного путя
     *
     * @return Объект [LangComponent]
     */
    fun getLangComponent(path: String?): LangComponent {
        return langComponents[path] ?: LangComponent(null, null)
    }

    /**
     * Возвращает [Component] из указанного путя в конфиге
     *
     * @return Объект [Component]
     */
    fun getComponent(path: String): Component {
        return miniMessage.deserialize(config.getString(path)!!)
    }

    /**
     * Сохраняет конфиг
     */
    protected fun save() {
        try {
            config.save(rawConfig)
        } catch (e: IOException) {
            throw RuntimeException(e)
        }
    }

    /**
     * Устанавливает значение в конфиге если оно не существует
     *
     * @param path Путь
     * @param o Объект для записи
     */
    protected fun setIfNotExists(path: String?, o: Any?) {
        if (!config.contains(path!!)) config[path] = o
    }

    /**
     * Вызывается при первой загрузке
     */
    protected open fun onFirstLoad() {}

    /**
     * Вызывается при каждой загрузке
     */
    protected open fun checkDefault() {}

    /**
     * Перезагрузка конфига
     */
    open fun reload() {
        this.config = loadConfiguration(rawConfig)
    }

    private fun loadConfiguration(file: File): YamlConfiguration {
        return YamlConfiguration.loadConfiguration(file)
    }
}