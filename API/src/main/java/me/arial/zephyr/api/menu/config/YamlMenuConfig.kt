package me.arial.zephyr.api.menu.config

import me.arial.zephyr.api.Extension.Companion.parseColor
import me.arial.zephyr.api.menu.item.BasicItem
import me.arial.zephyr.api.module.ZephyrModule
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.plugin.java.JavaPlugin
import java.io.File

/**
 * YAML конфигурация для меню
 *
 * @param config Объект конфигурации
 * @param file Файл конфигурации
 * @param parseColor Парсить ли цвета
 */
class YamlMenuConfig(
    val config: FileConfiguration,
    val file: File,
    val parseColor: Boolean = true
) : MenuConfig() {

    constructor(module: ZephyrModule, name: String, parseColor: Boolean = true)
            : this(
                YamlConfiguration.loadConfiguration(
                    File(module.dataFolder, name)
                ),
                File(module.dataFolder, name),
                parseColor
            )

    constructor(plugin: JavaPlugin, name: String, parseColor: Boolean = true)
            : this(
                YamlConfiguration.loadConfiguration(
                    File(plugin.dataFolder, name)
                ),
                File(plugin.dataFolder, name),
                parseColor
            )

    override val title: String
        get() = if(parseColor) (config.getString("title") ?: " ").parseColor() else config.getString("title") ?: " "

    override val items: MutableMap<String, BasicItem>
        get() {
            val result = mutableMapOf<String, BasicItem>()

            val section = config.getConfigurationSection("items") ?: return result

            section.getKeys(false).forEach {
                result[it] = BasicItem.deserialize(section.getConfigurationSection(it)!!)
            }

            return result
        }

    override val pattern: MutableList<String>
        get() = config.getStringList("pattern")

    override fun reload() {
        config.load(file)
    }
}