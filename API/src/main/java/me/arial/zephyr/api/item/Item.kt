//package me.arial.zephyr.api.item
//
//import net.kyori.adventure.text.Component
//import org.bukkit.Material
//import org.bukkit.inventory.ItemStack
//import org.bukkit.inventory.meta.ItemMeta
//
//class Item {
//
//    val item: ItemStack
//    val isCloned: Boolean
//
//    constructor(item: ItemStack?, clone: Boolean = false) {
//        if (item == null) {
//            this.item = ItemStack(Material.AIR)
//            isCloned = false
//        } else {
//            this.item = if (clone) item else item.clone()
//            isCloned = true
//        }
//    }
//
//    constructor(material: Material?) {
//        this.item = ItemStack(material ?: Material.STONE)
//        isCloned = false
//    }
//
//    var meta: ItemMeta
//        get() = item.itemMeta
//        set(value) {
//            item.itemMeta = value
//        }
//
//    var displayName: String
//        get() = if (meta.hasDisplayName()) meta.displayName else ""
//        set(value) {
//            val oldMeta = item.itemMeta
//            oldMeta.setDisplayName(value)
//            meta = oldMeta
//        }
//
//    var lore: MutableList<String?>
//        get() = if ((meta.lore == null)) mutableListOf() else meta.lore!!
//        set(value) {
//            val oldMeta = item.itemMeta
//            oldMeta.lore = value
//            meta = oldMeta
//        }
//
//    var componentLore: MutableList<Component?>
//        get() = if ((meta.lore() == null)) mutableListOf() else meta.lore()!!
//        set(value) {
//            val oldMeta = item.itemMeta
//            oldMeta.lore(value)
//            meta = oldMeta
//        }
//
//    fun addLore(vararg lore: String?): Item {
//        val oldLore = this.lore
//
//        lore.forEach {
//            oldLore.add(it)
//        }
//
//        this.lore = oldLore
//
//        return this
//    }
//
//
//}