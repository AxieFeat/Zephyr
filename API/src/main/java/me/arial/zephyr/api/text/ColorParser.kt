package me.arial.zephyr.api.text

import net.md_5.bungee.api.ChatColor
import java.util.regex.Pattern

/**
 * Класс парсера цветов
 */
class ColorParser {
    companion object {
        /**
         * Парсит цвета в сообщении с поддержкой HEX формата &#RRGGBB
         *
         * @param text Исходный текст
         * @return Изменённый текст
         */
        fun parseColor(text: String): String {
            val matcher = Pattern.compile("(&#[0-9a-fA-F]{6})").matcher(text)
            val sb = StringBuilder()

            while (matcher.find()) {
                val hex = matcher.group(1).substring(1)
                matcher.appendReplacement(sb, "" + ChatColor.of(hex))
            }

            matcher.appendTail(sb)
            val hexColored = sb.toString()

            return ChatColor.translateAlternateColorCodes('&', hexColored)
        }
    }
}