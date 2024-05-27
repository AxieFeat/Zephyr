package me.arial.zephyr.api.time

import org.bukkit.configuration.file.FileConfiguration

/**
 * Класс парсера времени
 *
 * @param configuration Объект конфигурации
 */
class TimeManager(val configuration: FileConfiguration) {

    /**
     * Парсит миллисекунды в строку
     *
     * Например:
     * 65000 -> 6 м. 5 с.
     *
     * @param prefix Путь в конфигурации до настроек формата
     * @param milliseconds Миллисекунды
     *
     * @return Строка со временем
     */
    fun parseTime(prefix: String, milliseconds: Long): String {
        val seconds = milliseconds / 1000
        if (seconds <= 0) return placeSeconds(prefix, configuration.getString("$prefix.config.onlySeconds")!!, 0)
        if (seconds < 60) return placeSeconds(prefix, configuration.getString("$prefix.config.onlySeconds")!!, seconds)

        val minutes = seconds / 60
        val newSeconds = seconds - (60 * minutes)
        if (seconds < 3600) {
            if (seconds % 60 == 0L) return placeMinutes(prefix, configuration.getString("$prefix.config.onlyMinutes")!!, minutes)

            val secondsPlaced = placeSeconds(prefix, configuration.getString("$prefix.config.secondsMinutes")!!, newSeconds)
            return placeMinutes(prefix, secondsPlaced, minutes)
        }

        val hours = seconds / 3600
        val newMinutes = minutes - (60 * hours)
        if (seconds < 86400) {
            if (newSeconds == 0L && newMinutes == 0L) return placeHours(
                prefix,
                configuration.getString("$prefix.config.onlyHours")!!,
                hours
            )
            if (newSeconds == 0L) {
                val minutesPlaced = placeMinutes(prefix, configuration.getString("$prefix.config.minutesHours")!!, newMinutes)
                return placeHours(prefix, minutesPlaced, hours)
            }
            if (newMinutes == 0L) {
                val secondsPlaced = placeSeconds(prefix,
                    configuration.getString("$prefix.config.secondsHours")!!, newSeconds)
                return placeHours(prefix, secondsPlaced, hours)
            }

            val secondsPlaced = placeSeconds(prefix, configuration.getString("$prefix.config.secondsMinutesHours")!!, newSeconds)
            val minutesPlaced = placeMinutes(prefix, secondsPlaced, newMinutes)
            return placeHours(prefix, minutesPlaced, hours)
        }

        val days = seconds / 86400
        val newHours = hours - (24 * days)
        if (newSeconds == 0L && newMinutes == 0L && newHours == 0L) return placeDays(
            prefix,
            configuration.getString("$prefix.config.onlyDays")!!,
            days
        )
        if (newSeconds == 0L && newMinutes == 0L) {
            val hoursPlaced = placeHours(prefix, configuration.getString("$prefix.config.hoursDays")!!, newHours)
            return placeDays(prefix, hoursPlaced, days)
        }
        if (newSeconds == 0L && newHours == 0L) {
            val minutesPlaced = placeMinutes(prefix, configuration.getString("$prefix.config.minutesDays")!!, newMinutes)
            return placeDays(prefix, minutesPlaced, days)
        }
        if (newMinutes == 0L && newHours == 0L) {
            val secondsPlaced = placeMinutes(prefix, configuration.getString("$prefix.config.secondsDays")!!, newSeconds)
            return placeDays(prefix, secondsPlaced, days)
        }

        if (newSeconds == 0L) {
            val minutesPlaced = placeMinutes(prefix, configuration.getString("$prefix.config.minutesHoursDays")!!, newMinutes)
            val hoursPlaced = placeHours(prefix, minutesPlaced, newHours)
            return placeDays(prefix, hoursPlaced, days)
        }
        if (newMinutes == 0L) {
            val secondsPlaced = placeSeconds(prefix, configuration.getString("$prefix.config.secondsHoursDays")!!, newSeconds)
            val hoursPlaced = placeHours(prefix, secondsPlaced, newHours)
            return placeDays(prefix, hoursPlaced, days)
        }
        if (newHours == 0L) {
            val secondsPlaced = placeSeconds(prefix, configuration.getString("$prefix.config.secondsMinutesDays")!!, newSeconds)
            val minutesPlaced = placeMinutes(prefix, secondsPlaced, newMinutes)
            return placeDays(prefix, minutesPlaced, days)
        }

        val secondsPlaced = placeSeconds(prefix, configuration.getString("$prefix.config.secondsMinutesHoursDays")!!, newSeconds)
        val minutesPlaced = placeMinutes(prefix, secondsPlaced, newMinutes)
        val hoursPlaced = placeHours(prefix, minutesPlaced, newHours)

        return placeDays(prefix, hoursPlaced, days)
    }

    private fun placeSeconds(prefix: String, message: String, seconds: Long): String {
        val time = message.replace("%seconds%", seconds.toString())
        return if (seconds == 1L) time.replace("%seconds_placeholder%", configuration.getString("$prefix.second")!!)
        else time.replace("%seconds_placeholder%", configuration.getString("$prefix.seconds")!!)
    }

    private fun placeMinutes(prefix: String, message: String, minutes: Long): String {
        val time = message.replace("%minutes%", minutes.toString())
        return if (minutes == 1L) time.replace("%minutes_placeholder%", configuration.getString("$prefix.minute")!!)
        else time.replace("%minutes_placeholder%", configuration.getString("$prefix.minutes")!!)
    }

    private fun placeHours(prefix: String, message: String, hours: Long): String {
        val time = message.replace("%hours%", hours.toString())
        return if (hours == 1L) time.replace("%hours_placeholder%", configuration.getString("$prefix.hour")!!)
        else time.replace("%hours_placeholder%", configuration.getString("$prefix.hours")!!)
    }

    private fun placeDays(prefix: String, message: String, days: Long): String {
        val time = message.replace("%days%", days.toString())
        return if (days == 1L) time.replace("%days_placeholder%", configuration.getString("$prefix.day")!!)
        else time.replace("%days_placeholder%", configuration.getString("$prefix.days")!!)
    }
}