package me.arial.zephyr.api.text

import me.arial.zephyr.api.replace
import me.arial.zephyr.api.replaceWithParse
import me.arial.zephyr.api.sound.WrappedSound
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.configuration.file.FileConfiguration

/**
 * Класс текстовых компонентов Zephyr
 *
 * @param components Компоненты из Adventure API
 * @param wrappedSound Звук, который будет отправлен вместе с сообщениями
 */
data class LangComponent(
    var components: MutableList<Component>?,
    var wrappedSound: WrappedSound?,
) {

    /**
     * Замена строк в [LangComponent]
     *
     * @param old Что нужно заменить
     * @param new Строка для замены
     *
     * @return Новый объект [LangComponent] с изменёнными значениями
     */
    fun replace(old: String, new: String): LangComponent {
        if (components == null) {
            return this.copy()
        }

        return LangComponent(
            components!!.map {
                it.replace(
                    old,
                    new
                )
            }.toMutableList(),
            wrappedSound
        )
    }

    /**
     * Замена строк в [LangComponent] с парсингом MiniMessage
     *
     * @param old Что нужно заменить
     * @param new Строка для замены
     *
     * @return Новый объект [LangComponent] с изменёнными значениями
     */
    fun replaceWithParse(old: String, new: String): LangComponent {
        if (components == null) {
            return this.copy()
        }

        return LangComponent(
            components!!.map {
                it.replaceWithParse(
                    old,
                    new
                )
            }.toMutableList(),
            wrappedSound
        )
    }


    /**
     * Копирование [LangComponent]
     *
     * @return Новый объект [LangComponent] с такими же значениями
     */
    fun copy(): LangComponent {
        return LangComponent(components, wrappedSound)
    }

    companion object {
        /**
         * Десериализует [LangComponent] из [FileConfiguration]
         *
         * @param config Файл конфигурации
         * @param path Путь до [LangComponent] в конфиге
         * @return Десериализованный [LangComponent]
         */
        fun deserialize(config: FileConfiguration, path: String?): LangComponent {
            if (path == null) {
                return LangComponent(null, null)
            }

            val components: MutableList<String> = mutableListOf()

            if (!config.isConfigurationSection(path)) {
                if (config.isString(path)) {
                    components.add(
                        config.getString(path)!!
                    )
                }
                if (config.isList(path)) {
                    config.getStringList(path).forEach {
                        components.add(it)
                    }
                }

                if (components.isNotEmpty()) {
                    return LangComponent(
                        components.map { deserializeComponent(it) }.toList().toMutableList(),
                        WrappedSound(null, 1f, 1f)
                    )
                }
                return LangComponent(
                    null,
                    WrappedSound(null, 1f, 1f)
                )
            }

            if (!config.isConfigurationSection("$path.sound")) {
                if (config.isString("$path.message")) {
                    components.add(
                        config.getString("$path.message")!!
                    )
                }
                if (config.isList("$path.message")) {
                    config.getStringList("$path.message").forEach {
                        components.add(it)
                    }
                }

                if (components.isNotEmpty()) {
                    return LangComponent(
                        components.map { deserializeComponent(it) }.toList().toMutableList(),
                        WrappedSound(
                            config.getString("$path.sound") ?: "ENTITY_DOLPHIN_PLAY",
                            1f, 1f
                        )
                    )
                }
                return LangComponent(
                    null,
                    WrappedSound(
                        config.getString("$path.sound") ?: "ENTITY_DOLPHIN_PLAY",
                        1f, 1f
                    )
                )
            }

            if (config.isString("$path.message")) {
                components.add(
                    config.getString("$path.message")!!
                )
            }
            if (config.isList("$path.message")) {
                config.getStringList("$path.message").forEach {
                    components.add(it)
                }
            }

            if (components.isNotEmpty()) {
                return LangComponent(
                    components.map { deserializeComponent(it) }.toList().toMutableList(),
                    WrappedSound(
                        config.getString("$path.sound.type") ?: "ENTITY_DOLPHIN_PLAY",
                        config.getDouble("$path.sound.volume").toFloat(),
                        config.getDouble("$path.sound.pitch").toFloat()
                    )
                )
            }
            return LangComponent(
                null,
                WrappedSound(
                    config.getString("$path.sound.type") ?: "ENTITY_DOLPHIN_PLAY",
                    config.getDouble("$path.sound.volume").toFloat(),
                    config.getDouble("$path.sound.pitch").toFloat()
                )
            )
        }

        private val miniMessage = MiniMessage.builder().build()

        private fun deserializeComponent(component: String): Component {
            return miniMessage.deserialize(component)
        }
    }
}