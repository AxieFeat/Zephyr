package me.arial.zephyr.api.menu.item

import me.arial.zephyr.api.text.ColorParser
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.Color
import org.bukkit.Material
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.LeatherArmorMeta
import org.bukkit.inventory.meta.PotionMeta
import xyz.xenondevs.invui.item.Click
import xyz.xenondevs.invui.item.ItemProvider
import xyz.xenondevs.invui.item.ItemWrapper
import xyz.xenondevs.invui.item.impl.AbstractItem
import java.util.*
import java.util.function.Consumer

/**
 * Базовый предмет для меню
 */
abstract class BasicItem(
    private var itemProvider: ItemProvider?,
    private var clickHandler: Consumer<Click>? = null,
) : AbstractItem() {

    constructor(
        itemStack: ItemStack
    ): this(ItemWrapper(itemStack))

    constructor(
        itemStack: ItemStack,
        meta: MutableMap<String, String>
    ): this(ItemWrapper(itemStack)) {
        this.meta = meta
    }

    override fun getItemProvider(): ItemProvider? {
        return this.itemProvider
    }

    /**
     * META информация, сохранённая в предмете
     */
    open var meta = mutableMapOf<String, String>()

    private var base64Head: String? = null

    /**
     * Установка base64 текстуры на голову
     *
     * <b>!ВАЖНО! Текстура должна устанавливаться в первую очередь</b>
     *
     * @param base64 Base64 текстура головы
     */
    private fun setHeadBase64(base64: String?) {
        if (base64 == null) {
            return
        }

        Companion.setHeadBase64(itemProvider?.get()!!, base64)
    }

    /**
     * Установка свечения для предмета путём добавления для него зачарования [Enchantment.PROTECTION_ENVIRONMENTAL] и флага [ItemFlag.HIDE_ENCHANTS]
     */
    open var glow: Boolean
        get() {
            val meta = itemProvider!!.get().itemMeta

            return meta.hasEnchant(Enchantment.PROTECTION_ENVIRONMENTAL) && meta.hasItemFlag(ItemFlag.HIDE_ENCHANTS)
        }
        set(value) {
            val meta = itemProvider!!.get().itemMeta

            meta.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 1, true)
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS)

            itemProvider!!.get().itemMeta = meta
        }

    /**
     * Установка цвета для предмета
     * (Только для кожаной брони и зелий)
     */
    open var color: Color?
        get() {
            val meta = itemProvider!!.get().itemMeta

            return when {
                meta is PotionMeta -> meta.basePotionData.type.effectType?.color
                meta is LeatherArmorMeta -> meta.color
                else -> null
            }
        }
        set(value) {
            val meta = itemProvider!!.get().itemMeta

            when {
                meta is PotionMeta -> {
                    meta.color = value
                }
                meta is LeatherArmorMeta -> {
                    meta.setColor(value)
                }
                else -> {}
            }
            itemProvider!!.get().itemMeta = meta
        }

    /**
     * Прочность предмета
     */
    open var durability: Short
        get() {
            return itemProvider!!.get().durability
        }
        set(value) {
            itemProvider!!.get().durability = value
        }

    /**
     * Лор предмета
     */
    open var lore: MutableList<Component>?
        get() {
            val meta = itemProvider!!.get().itemMeta

            return meta.lore()
        }
        set(value) {
            val meta = itemProvider!!.get().itemMeta

            if (value != null) {
                meta.lore()?.clear()
                meta.lore()?.addAll(value)
            } else {
                meta.lore = null
            }

            itemProvider!!.get().itemMeta = meta
        }

    /**
     * Материал предмета
     */
    open var type: Material
        get() = itemProvider!!.get().type
        set(value) {
            itemProvider!!.get().type = value
        }

    /**
     * Название предмета
     */
    open var name: Component?
        get() = itemProvider!!.get().itemMeta.displayName()
        set(value) {
            val meta = itemProvider!!.get().itemMeta

            meta.displayName(value)

            itemProvider!!.get().itemMeta = meta
        }

    private var onClick: Consumer<InventoryClickEvent> = Consumer { e: InventoryClickEvent -> }

    override fun handleClick(clickType: ClickType, player: Player, event: InventoryClickEvent) {
        onClick.accept(event)
        onClick(onClick)

        if (this.clickHandler != null) {
            clickHandler?.accept(Click(event))
        }
    }

    /**
     * Ивент клика по этому предмету
     */
    open fun onClick(event: Consumer<InventoryClickEvent>) {
        this.onClick = event
    }

    /**
     * Сериализация предмета в секцию конфигурации
     */
    open fun serialize(section: ConfigurationSection) {
        val item = itemProvider!!.get()
        val meta = item.itemMeta

        section.set("material", item.type.toString())

        if (meta.hasDisplayName()) {
            section.set("name", meta.displayName)
        }

        if (base64Head != null) section.set("skull-meta", base64Head)

        section.set("durability", item.durability)

        if (meta.hasLore()) {
            section.set("lore", meta.lore)
        }

        if (meta is PotionMeta) {
            section.set("color.r", meta.basePotionData.type.effectType?.color?.red)
            section.set("color.g", meta.basePotionData.type.effectType?.color?.green)
            section.set("color.b", meta.basePotionData.type.effectType?.color?.blue)
        }

        if (meta is LeatherArmorMeta) {
            section.set("color.r", meta.color.red)
            section.set("color.g", meta.color.green)
            section.set("color.b", meta.color.blue)
        }

        if (glow) {
            section.set("glow", true)
        }

        val itemFlags = mutableListOf<String>()

        val isGlow = glow

        meta.itemFlags.forEach {
            if (isGlow && it != ItemFlag.HIDE_ENCHANTS) {
                itemFlags.add(it.name)
            } else {
                itemFlags.add(it.name)
            }
        }

        if (itemFlags.isNotEmpty()) {
            section.set("item-flags", itemFlags)
        }

        if (this.meta.isNotEmpty()) {
            section.set("meta", this.meta)
        }

        modifySerialization(section)
    }

    /**
     * Сериализация предмета в мапу, для сохранения в любую структуру данных
     *
     * @return Мапа со значениями предмета
     */
    open fun serialize(): MutableMap<String, Any> {
        val result = mutableMapOf<String, Any>()

        val item = itemProvider!!.get()
        val meta = item.itemMeta

        result["material"] = item.type

        if (meta.hasDisplayName()) {
            result["name"] = meta.displayName
        }

        if (base64Head != null) {
            result["skull-meta"] = base64Head!!
        }

        result["durability"] = item.durability

        if (meta.hasLore()) {
            result["lore"] = meta.lore!!
        }

        if (meta is PotionMeta) {
            val colors = mutableMapOf<String, Any>()

            colors["r"] = meta.basePotionData.type.effectType?.color?.red!!
            colors["g"] = meta.basePotionData.type.effectType?.color?.green!!
            colors["b"] = meta.basePotionData.type.effectType?.color?.blue!!

            result["color"] = colors
        }

        if (meta is LeatherArmorMeta) {
            val colors = mutableMapOf<String, Any>()

            colors["r"] = meta.color.red
            colors["g"] = meta.color.green
            colors["b"] = meta.color.blue

            result["color"] = colors
        }

        if (glow) {
            result["glow"] = true
        }

        val itemFlags = mutableListOf<String>()

        val isGlow = glow

        meta.itemFlags.forEach {
            if (isGlow && it != ItemFlag.HIDE_ENCHANTS) {
                itemFlags.add(it.name)
            } else {
                itemFlags.add(it.name)
            }
        }

        if (itemFlags.isNotEmpty()) {
            result["item-flags"] = itemFlags
        }

        if (this.meta.isNotEmpty()) {
            result["meta"] = this.meta
        }

        return result
    }

    /**
     * Модификатор процесса сериализации предмета в секцию конфигураци
     *
     * @param section Секция конфигурации
     */
    open fun modifySerialization(section: ConfigurationSection) {}

    companion object {
        /**
         * Десериализация предмета из секции конфигурации
         *
         * @param section Секция конфигурации
         *
         * @return Анонимный объект [BasicItem]
         */
        fun deserialize(section: ConfigurationSection): BasicItem {
            val item = ItemStack(Material.valueOf(section.getString("material")!!))
            val meta = item.itemMeta

            if (item.type == Material.PLAYER_HEAD && section.contains("skull-meta")) {
                setHeadBase64(item, section.getString("skull-meta"))
            }

            when(meta) {
                is PotionMeta -> {
                    if (section.contains("color")) {
                        meta.color = Color.fromRGB(
                            section.getInt("color.r"),
                            section.getInt("color.g"),
                            section.getInt("color.b")
                        )
                    }
                }
                is LeatherArmorMeta -> {
                    if (section.contains("color")) {
                        meta.setColor(
                            Color.fromRGB(
                                section.getInt("color.r"),
                                section.getInt("color.g"),
                                section.getInt("color.b")
                            )
                        )
                    }
                }
            }

            if (section.contains("lore")) {
                val lines: MutableList<String> = mutableListOf()

                section.getStringList("lore").forEach {
                    lines.add(ColorParser.parseColor(it))
                }

                meta.lore = lines
            }

            val basicItemMeta: MutableMap<String, String> = mutableMapOf()

            if (section.contains("meta")) {
                section.getMapList("meta").forEach { map ->
                    map.firstNotNullOfOrNull {
                        val key = it.key as String
                        val value = it.value as String

                        basicItemMeta[key] = value
                    }
                }
            }

            if (section.contains("glow") && section.getBoolean("glow")) {
                meta.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 1, true)
                meta.addItemFlags(ItemFlag.HIDE_ENCHANTS)
            }

            if (section.contains("durability")) {
                item.durability = section.getInt("durability").toShort()
            }

            if (section.contains("name")) {
                meta.setDisplayName(ColorParser.parseColor(section.getString("name")!!))
            }

            if (section.contains("item-flags")) {
                section.getStringList("item-flags").forEach {
                    meta.addItemFlags(ItemFlag.valueOf(it))
                }
            }

            item.itemMeta = meta

            return object : BasicItem(item, basicItemMeta) {}
        }

        /**
         * Десериализация предмета из мапы значений
         *
         * @param map Мапа с данными о предмете
         *
         * @return Анонимный объект [BasicItem]
         */
        fun deserialize(map: MutableMap<String, Any>): BasicItem {
            val item = ItemStack(Material.valueOf(map["material"] as String))
            val meta = item.itemMeta

            if (item.type == Material.PLAYER_HEAD && map.containsKey("skull-meta")) {
                setHeadBase64(item, (map["material"] as String))
            }

            when(meta) {
                is PotionMeta -> {
                    if (map.containsKey("color")) {
                        val colors = map["color"] as MutableMap<String, Any>

                        meta.color = Color.fromRGB(
                            colors["r"] as Int,
                            colors["g"] as Int,
                            colors["b"] as Int
                        )
                    }
                }
                is LeatherArmorMeta -> {
                    if (map.containsKey("color")) {
                        val colors = map["color"] as MutableMap<String, Any>

                        meta.setColor(
                            Color.fromRGB(
                                colors["r"] as Int,
                                colors["g"] as Int,
                                colors["b"] as Int
                            )
                        )
                    }
                }
            }

            if (map.containsKey("lore")) {
                meta.lore = map["lore"] as MutableList<String>
            }

            val basicItemMeta: MutableMap<String, String> = mutableMapOf()

            if (map.containsKey("meta")) {
                basicItemMeta.putAll(map["meta"] as MutableMap<String, String>)
            }

            if (map.containsKey("glow") && (map["glow"] as Boolean)) {
                meta.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 1, true)
                meta.addItemFlags(ItemFlag.HIDE_ENCHANTS)
            }

            if (map.containsKey("durability")) {
                item.durability = map["durability"] as Short
            }

            if (map.containsKey("name")) {
                meta.setDisplayName(ColorParser.parseColor(map["name"] as String))
            }

            if (map.containsKey("item-flags")) {
                (map["item-flags"] as MutableList<String>).forEach {
                    meta.addItemFlags(ItemFlag.valueOf(it))
                }
            }

            item.itemMeta = meta

            return object : BasicItem(item, basicItemMeta) {}
        }

        private fun setHeadBase64(item: ItemStack, base64: String?) {
            if (base64 == null) {
                return
            }

            val hashAsId = UUID(base64.hashCode().toLong(), base64.hashCode().toLong())

            Bukkit.getUnsafe().modifyItemStack(
                item,
                "{SkullOwner:{Id:\"$hashAsId\",Properties:{textures:[{Value:\"$base64\"}]}}}"
            )
        }
    }
}