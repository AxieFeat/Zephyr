package me.arial.zephyr.api.item

import me.arial.zephyr.api.getComponent
import me.arial.zephyr.api.getComponentList
import me.arial.zephyr.api.menu.item.BasicItem
import me.arial.zephyr.api.serializeComponent
import me.arial.zephyr.api.text.ColorParser
import me.arial.zephyr.api.text.LangComponent
import net.kyori.adventure.text.Component
import org.bukkit.*
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.enchantments.Enchantment
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta
import org.bukkit.inventory.meta.LeatherArmorMeta
import org.bukkit.inventory.meta.SkullMeta
import java.util.*
import java.util.function.Function
import java.util.stream.Collectors

/**
 * Класс для быстрого создания айтемстаков
 */
class ItemBuilder {

    private var item: ItemStack? = null
    var clickSound: Sound? = null
        private set


    /**
     * @param item Предмет
     * @param pure Копировать ли этот предмет
     */
    constructor(item: ItemStack?, pure: Boolean) {
        if (item == null) {
            this.item = ItemStack(Material.AIR)
        } else {
            this.item = if (pure) item else item.clone()
        }
    }

    /**
     * @param item Предмет
     */
    constructor(item: ItemStack?) {
        this.item = item
    }

    /**
     * Создаёт предмет с указанным материалом
     *
     * @param material Материал
     */
    constructor(material: Material?) {
        this.item = ItemStack(material!!)
    }

    /**
     * @return [ItemMeta] айтемстака
     */
    fun meta(): ItemMeta? {
        return item!!.itemMeta
    }

    /**
     * Установить [ItemMeta] для айтемстака
     *
     * @param meta [ItemMeta]
     *
     * @return Текущий объект [ItemBuilder]
     */
    fun meta(meta: ItemMeta?): ItemBuilder {
        item!!.setItemMeta(meta)
        return this
    }

    /**
     * @return Имя айтемстака
     */
    fun name(): String {
        return if (meta()!!.hasDisplayName()) meta()!!.displayName else ""
    }

    /**
     * Установить имя для айтемстака
     *
     * @param name Имя
     *
     * @return Текущий объект [ItemBuilder]
     */
    fun name(name: String?): ItemBuilder {
        val meta = meta()
            ?: return this

        meta.setDisplayName(name)

        meta(meta)
        return this
    }

    /**
     * Установить имя для айтемстака
     *
     * @param name Имя
     *
     * @return Текущий объект [ItemBuilder]
     */
    fun name(name: Component?): ItemBuilder {
        val meta = meta()
            ?: return this

        meta.displayName(name)

        meta(meta)
        return this
    }

    /**
     * @return Лор айтемстака
     */
    fun lore(): MutableList<String?> {
        return if ((meta() == null || meta()!!.lore == null)) ArrayList() else meta()!!.lore!!
    }

    /**
     * @return Лор айтемстака
     */
    fun componentLore(): MutableList<Component?> {
        return if ((meta() == null || meta()!!.lore() == null)) ArrayList() else meta()!!.lore()!!
    }

    /**
     * Установить лор для айтемстака
     *
     * @param lore Лор
     *
     * @return Текущий объект [ItemBuilder]
     */
    fun lore(lore: List<String>?): ItemBuilder {
        val meta = meta()
            ?: return this

        meta.lore = lore
        meta(meta)

        return this
    }

    /**
     * Установить лор для айтемстака
     *
     * @param lore Лор
     *
     * @return Текущий объект [ItemBuilder]
     */
    fun componentLore(lore: List<Component>?): ItemBuilder {
        val meta = meta()
            ?: return this

        meta.lore(lore)
        meta(meta)

        return this
    }

    /**
     * Добавить строки для лора
     *
     * @param lore Строки
     *
     * @return Текущий объект [ItemBuilder]
     */
    fun addLore(vararg lore: String?): ItemBuilder {
        val meta = meta()
            ?: return this

        val old = lore()
        for (line in lore) {
            old.add(line)

        }
        meta.lore = old
        meta(meta)

        return this
    }

    /**
     * Добавить строки для лора
     *
     * @param lore Строки
     *
     * @return Текущий объект [ItemBuilder]
     */
    fun addLore(vararg lore: Component?): ItemBuilder {
        val meta = meta()
            ?: return this

        val old = componentLore()
        for (line in lore) {
            old.add(line)
        }
        meta.lore(old)
        meta(meta)

        return this
    }

    /**
     * Добавить LangComponent'ы для лора
     *
     * @param lore [LangComponent]'ы
     *
     * @return Текущий объект [ItemBuilder]
     */
    fun addLore(vararg lore: LangComponent): ItemBuilder {
        val meta = meta()
            ?: return this

        val old = meta.lore()

        lore.forEach { langComponent ->
            if (langComponent.components != null) {
                langComponent.components!!.forEach { component ->
                    old!!.add(component)
                }
            }
        }

        meta.lore(old)

        meta(meta)

        return this
    }

    /**
     * Добавить строки для лора сверху
     *
     * @param lore Строки
     *
     * @return Текущий объект [ItemBuilder]
     */
    fun addLoreAbove(vararg lore: String?): ItemBuilder {
        val meta = meta()
            ?: return this

        val old = lore()
        val toAdd = Arrays.asList(*lore)
        Collections.reverse(toAdd)
        old.addAll(0, toAdd)
        meta.lore = old
        meta(meta)

        return this
    }

    /**
     * Добавить строки для лора сверху
     *
     * @param lore Строки
     *
     * @return Текущий объект [ItemBuilder]
     */
    fun addLoreAbove(vararg lore: Component?): ItemBuilder {
        val meta = meta()
            ?: return this

        val old = componentLore()
        val toAdd = Arrays.asList(*lore)
        toAdd.reverse()
        old.addAll(0, toAdd)
        meta.lore(old)
        meta(meta)

        return this
    }

    /**
     * Установить количество для айтемстака
     *
     * @param amount количество
     *
     * @return Текущий объект [ItemBuilder]
     */
    fun amount(amount: Int): ItemBuilder {
        item!!.amount = amount
        return this
    }

    /**
     * @return Количество айтемстака
     */
    fun amount(): Int {
        return item!!.amount
    }

    /**
     * Установить свечение для айтемстака
     *
     * @param glow Свечение (вкл/выкл)
     *
     * @return Текущий объект [ItemBuilder]
     */
    fun glow(glow: Boolean): ItemBuilder {
        val meta = meta()

        if (meta == null || !glow) return this

        meta.addEnchant(Enchantment.DURABILITY, 1, true)
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS)
        meta(meta)

        return this
    }

    /**
     * Установить цвет кожаной брони для айтемстака
     *
     * @param color Цвет
     *
     * @return Текущий объект [ItemBuilder]
     */
    fun setLeatherColor(color: Color?): ItemBuilder {
        if (color == null || meta() !is LeatherArmorMeta) return this

        val meta = meta() as LeatherArmorMeta?
        meta!!.setColor(color)
        meta(meta)

        return this
    }

    /**
     * Скрыть флаги
     *
     * @return Текущий объект [ItemBuilder]
     */
    fun hideFlags(): ItemBuilder {
        val meta = meta()
            ?: return this

        meta.addItemFlags(*ItemFlag.entries.toTypedArray())
        meta(meta)

        return this
    }

    /**
     * Добавить флаги для для айтемстака
     *
     * @param flags Флаги
     *
     * @return Текущий объект [ItemBuilder]
     */
    fun flag(vararg flags: ItemFlag): ItemBuilder {
        val meta = meta()
            ?: return this

        meta.addItemFlags(*flags)
        meta(meta)

        return this
    }

    /**
     * Добавить флаги для для айтемстака
     *
     * @param flags Флаги
     *
     * @return Текущий объект [ItemBuilder]
     */
    fun flag(flags: Collection<ItemFlag?>): ItemBuilder {
        val meta = meta()
            ?: return this

        flags.forEach { itemFlags: ItemFlag? ->
            meta.addItemFlags(
                itemFlags!!
            )
        }
        meta(meta)

        return this
    }

    /**
     * Установить владельца для айтемстака
     *
     * @param player Игрок
     *
     * @return Текущий объект [ItemBuilder]
     */
    fun setOwner(player: OfflinePlayer?): ItemBuilder {
        val meta = meta() as? SkullMeta
            ?: return this

        meta.setOwningPlayer(player)
        meta(meta)

        return this
    }

    /**
     * Установить звук нажатия по айтемстаку
     *
     * @param sound Звук
     *
     * @return Текущий объект [ItemBuilder]
     */
    fun setClickSound(sound: Sound?): ItemBuilder {
        this.clickSound = sound

        return this
    }

    /**
     * Установить текстуру для головы.
     * Устанавливайте текстуру в первую очередь
     *
     * @param base64 Текстура
     *
     * @return Текущий объект [ItemBuilder]
     */
    fun setHeadBase64(base64: String?): ItemBuilder {
        if (base64 == null) {
            return this
        }

        val hashAsId = UUID(base64.hashCode().toLong(), base64.hashCode().toLong())

        item = Bukkit.getUnsafe().modifyItemStack(
            item,
            "{SkullOwner:{Id:\"$hashAsId\",Properties:{textures:[{Value:\"$base64\"}]}}}"
        )

        return this
    }

    /**
     * ItemBuilder в ItemStack
     *
     * @return Текущий объект [ItemStack]
     */
    fun build(): ItemStack {
        return ItemStack(item!!)
    }

    /**
     * Сериализация [ItemBuilder] в конфигурацию
     *
     * @return Текущий объект [ItemBuilder]
     */
    fun save(section: ConfigurationSection): ItemBuilder {
        section["material"] = item!!.type.name
        val meta = meta()
        if (meta != null) {
            section["name"] = "<reset>" + meta.displayName()?.serializeComponent()
            section["lore"] = meta.lore()?.map { "<reset>" + it.serializeComponent() }

            if (meta is LeatherArmorMeta) {
                val color = meta.color
                section["armor-color.r"] = color.red
                section["armor-color.g"] = color.green
                section["armor-color.b"] = color.blue
            }
        }

        if (clickSound != null) {
            section["click-sound"] = clickSound!!.name
        }

        return this
    }

    fun asMenuItem(): BasicItem {
        return object : BasicItem(item!!) {}
    }

    companion object {
        /**
         * Получение [ItemBuilder] из секции конфигурации
         *
         * @param section Секция конфигурации
         *
         * @return Текущий объект [ItemBuilder]
         */
        fun fromConfig(section: ConfigurationSection): ItemBuilder {
            val material = Material.valueOf(section.getString("material", "AIR")!!)
            val name = section.getComponent("name")

            val lore: List<Component> = section.getComponentList("lore")

            var leatherColor: Color? = null
            if (section.contains("armor-color")) {
                val r = section.getInt("armor-color.r")
                val g = section.getInt("armor-color.g")
                val b = section.getInt("armor-color.b")

                leatherColor = Color.fromRGB(r, g, b)
            }

            var clickSound: Sound? = null
            if (section.contains("click-sound")) {
                clickSound = Sound.valueOf(section.getString("click-sound")!!)
            }

            var base64: String? = null
            if (section.contains("skill-meta") && material.toString() == "PLAYER_HEAD") {
                base64 = section.getString("skill-meta")
            }

            val flags = section.getStringList("flags").stream().map { name: String? ->
                ItemFlag.valueOf(
                    name!!
                )
            }.toList()

            val glow = section.getBoolean("glow", false)

            return ItemBuilder(material)
                .setHeadBase64(base64)
                .name(name)
                .componentLore(lore)
                .flag(flags)
                .glow(glow)
                .setLeatherColor(leatherColor)
                .setClickSound(clickSound)
        }

        /**
         * Получение [ItemBuilder] из секции конфигурации
         *
         * @param section Секция конфигурации
         * @param transformer Функция
         *
         * @return Текущий объект [ItemBuilder]
         */
        fun fromConfig(section: ConfigurationSection, transformer: Function<Component?, Component>): ItemBuilder {
            val material = Material.valueOf(section.getString("material", "AIR")!!)

            val name = transformer.apply(section.getComponent("name", ""))

            val lore = section.getComponentList("lore").stream()
                .map { line: Component? ->
                    transformer.apply(
                        line
                    )
                }
                .collect(Collectors.toList())

            var leatherColor: Color? = null
            if (section.contains("armor-color")) {
                val r = section.getInt("armor-color.r")
                val g = section.getInt("armor-color.g")
                val b = section.getInt("armor-color.b")

                leatherColor = Color.fromRGB(r, g, b)
            }

            var clickSound: Sound? = null
            if (section.contains("click-sound")) {
                clickSound = Sound.valueOf(section.getString("click-sound")!!)
            }

            var base64: String? = null
            if (section.contains("skill-meta") && material.toString() == "PLAYER_HEAD") {
                base64 = section.getString("skill-meta")
            }

            val flags = section.getStringList("flags").stream().map { name: String? ->
                ItemFlag.valueOf(
                    name!!
                )
            }.toList()

            val glow = section.getBoolean("glow", false)

            return ItemBuilder(material)
                .setHeadBase64(base64)
                .name(name)
                .componentLore(lore)
                .flag(flags)
                .glow(glow)
                .setLeatherColor(leatherColor)
                .setClickSound(clickSound)
        }
    }
}