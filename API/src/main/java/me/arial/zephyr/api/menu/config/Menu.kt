package me.arial.zephyr.api.menu.config

import me.arial.zephyr.api.menu.item.BasicItem
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import xyz.xenondevs.invui.gui.Gui
import xyz.xenondevs.invui.window.Window

/**
 * Класс для создания меню на основе библиотеки [xyz.xenondevs.invui]
 *
 * @param menuConfig Конфигурация меню
 */
abstract class Menu(
    private val menuConfig: MenuConfig
) {

    /**
     * Объект Gui из библиотеки InvUI
     */
    open var gui: Gui? = null

    /**
     * META информация для меню, в неё можно сохранять любые значения
     */
    open val meta: MutableMap<String, String> = mutableMapOf()

    /**
     * Название меню
     */
    open var title: String = menuConfig.title

    /**
     * Предметы меню
     */
    open val items: MutableMap<String, BasicItem> = menuConfig.items

    /**
     * Паттерн меню
     */
    open var pattern: MutableList<String> = menuConfig.pattern

    init {
        val guiBuilder = Gui.normal()
            .addModifier { guiModifier(it) }
            .setStructure(*pattern.toTypedArray())

        items.forEach {
            guiBuilder.addIngredient(it.key.first(), it.value)
        }

        gui = guiBuilder.build()

        onLoad()
    }

    open fun onLoad() {}

    /**
     * Перезагрузка меню (Меню создаётся с нуля)
     */
    open fun reload() {
        menuConfig.reload()

        title = menuConfig.title
        pattern = menuConfig.pattern

        val guiBuilder = Gui.normal()
            .addModifier { guiModifier(it) }
            .setStructure(*pattern.toTypedArray())

        items.forEach {
            guiBuilder.addIngredient(it.key.first(), it.value)
        }

        gui = guiBuilder.build()

        onLoad()
    }

    /**
     * Открытие меню для игрока
     *
     * @param player Объект игрока
     *
     * @return Объект [Window] из библиотеки InvUI
     */
    open fun open(player: Player): Window {
        val window = Window.single()
            .setViewer(player)
            .addModifier { windowModifier(it) }
            .setTitle(title)
            .setGui(gui!!)
            .addCloseHandler { onClose(player) }
            .addOpenHandler { onOpen(player) }
            .addOutsideClickHandler { onOutsideClick(it) }
            .build()

        window.open()

        return window
    }

    /**
     * Модификатор [Gui]
     */
    open fun guiModifier(gui: Gui) {}

    /**
     * Модификатор [Window]
     */
    open fun windowModifier(window: Window) {}

    /**
     * Ивент открытия меню
     */
    open fun onOpen(player: Player) {}

    /**
     * Ивент закрытия меню
     */
    open fun onClose(player: Player) {}

    /**
     * Ивент клика ВНЕ меню
     */
    open fun onOutsideClick(e: InventoryClickEvent) {}
}