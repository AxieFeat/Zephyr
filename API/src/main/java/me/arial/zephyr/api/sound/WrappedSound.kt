package me.arial.zephyr.api.sound

import org.bukkit.Sound
import org.bukkit.entity.Player

/**
 * Класс-обертка поверх звука от Bukkit
 *
 * @param sound Тип звука
 * @param volume Volume звука
 * @param pitch Pitch звука
 */
data class WrappedSound(val sound: Sound?, val volume: Float, val pitch: Float) {
    constructor(sound: String, volume: Float, pitch: Float): this(
        Sound.valueOf(sound),
        volume, pitch
    )

    /**
     * Отправка звука игрока (Если звук не равен null)
     *
     * @param player Объект игрока
     */
    fun play(player: Player) {
        if (sound != null) {
            player.playSound(
                player.location,
                sound, volume, pitch
            )
        }
    }
}