package me.arial.zephyr.api.item

import org.bukkit.inventory.ItemStack
import org.bukkit.util.io.BukkitObjectInputStream
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder
import java.io.ByteArrayInputStream

/**
 * Класс для работы с айтемстаками
 */
class ItemStackUtil {
    companion object {

        /**
         * Десериализация [ItemStack] из base64 строки
         *
         * @param base64 base64 строка
         *
         * @return Объект [ItemStack]
         */
        fun deserialize(base64: String): ItemStack? {
            try {
                ByteArrayInputStream(Base64Coder.decodeLines(base64)).use { inputStream ->
                    BukkitObjectInputStream(inputStream).use { dataInput ->
                        val items =
                            arrayOfNulls<ItemStack>(dataInput.readInt())
                        for (i in items.indices) items[i] = dataInput.readObject() as ItemStack

                        if (items.isNotEmpty() && items[0] != null) {
                            return items[0]
                        }
                        return null
                    }
                }
            } catch (ignored: Exception) {
                return null
            }
        }
    }
}