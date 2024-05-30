package me.arial.zephyr.api.debug

import me.arial.zephyr.api.sendColoredMessage
import org.bukkit.Bukkit
import org.bukkit.entity.Player

/**
 * Уровень дебага
 */
var debugLevel: DebugLevel = DebugLevel.NONE

/**
 * Отправляет дебаг сообщение в консоль
 * (Игнорирует уровень дебага)
 *
 * @param text Текст для дебага
 */
fun debug(text: String) {

    val stackTraceElements = Thread.currentThread().stackTrace

    val caller = stackTraceElements[2]

    println("[${caller.className}:${caller.methodName}:${caller.lineNumber}] $text")

}

/**
 * Отправляет дебаг сообщение в консоль
 *
 * @param text Текст для дебага
 * @param level Уровень дебага, при котором будет отправлено
 */
fun debug(text: String, level: DebugLevel) {
    if (Debug.isCorrectLevel(level)) {
        val stackTraceElements = Thread.currentThread().stackTrace

        val caller = stackTraceElements[2]

        println("[${caller.className}:${caller.methodName}:${caller.lineNumber}] $text")
    }
}

/**
 * Отправляет дебаг сообщение в консоль и всем игрокам на сервере
 *
 * @param text Текс для дебага
 * @param level Уровень дебага, при котором будет отправлено
 */
fun debugAll(text: String, level: DebugLevel) {
    if (Debug.isCorrectLevel(level)) {
        val stackTraceElements = Thread.currentThread().stackTrace

        val caller = stackTraceElements[2]

        Bukkit.broadcastMessage("[${caller.className}:${caller.methodName}:${caller.lineNumber}] $text")
    }
}

/**
 * Отправляет дебаг сообщение в консоль и всем игрокам на сервере
 * (Игнорирует уровень дебага)
 *
 * @param text Текс для дебага
 */
fun debugAll(text: String) {
    val stackTraceElements = Thread.currentThread().stackTrace

    val caller = stackTraceElements[2]

    Bukkit.broadcastMessage("[${caller.className}:${caller.methodName}:${caller.lineNumber}] $text")
}

class Debug {
    companion object {
        /**
         * Отправляет дебаг сообщение игроку
         *
         * @param text Текст для дебага
         * @param level Уровень дебага, при котором будет отправлено
         */
        fun Player.debug(text: String, level: DebugLevel) {
            if (isCorrectLevel(level)) {
                val stackTraceElements = Thread.currentThread().stackTrace

                val caller = stackTraceElements[2]

                sendColoredMessage("[${caller.className}:${caller.methodName}:${caller.lineNumber}] $text")
            }
        }

        /**
         * Отправляет дебаг сообщение игроку
         * (Игнорирует уровень дебага)
         *
         * @param text Текст для дебага
         */
        fun Player.debug(text: String) {
            val stackTraceElements = Thread.currentThread().stackTrace

            val caller = stackTraceElements[2]

            sendColoredMessage("[${caller.className}:${caller.methodName}:${caller.lineNumber}] $text")
        }

        /**
         * Функция для проверки, является ли дебаг уровень корректным
         *
         * @param level Уровень дебага
         * @return true, если текущий уровень дебага выше требуемого, и false, если нет
         */
        fun isCorrectLevel(level: DebugLevel): Boolean {
            when(level) {
                DebugLevel.NONE -> return true
                DebugLevel.LOW -> {
                    if (debugLevel != DebugLevel.NONE) return true
                }
                DebugLevel.MEDIUM -> {
                    if (debugLevel == DebugLevel.HIGH || debugLevel == DebugLevel.MEDIUM) return true
                }
                DebugLevel.HIGH -> {
                    if (debugLevel == DebugLevel.HIGH) return true
                }
            }

            return false
        }
    }
}

enum class DebugLevel {
    NONE,
    LOW,
    MEDIUM,
    HIGH
}