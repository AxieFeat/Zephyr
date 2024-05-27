package me.arial.zephyr.core.config

import me.arial.zephyr.api.configuration.Config
import me.arial.zephyr.api.time.TimeManager
import me.arial.zephyr.core.Zephyr

class FormatsConfig : Config(Zephyr.instance, "formats.yml") {
    override fun onFirstLoad() {
        setIfNotExists("formats.time-format.default.second", "с.")
        setIfNotExists("formats.time-format.default.seconds", "с.")
        setIfNotExists("formats.time-format.default.minute", "м.")
        setIfNotExists("formats.time-format.default.minutes", "м.")
        setIfNotExists("formats.time-format.default.hour", "ч.")
        setIfNotExists("formats.time-format.default.hours", "ч.")
        setIfNotExists("formats.time-format.default.day", "д.")
        setIfNotExists("formats.time-format.default.days", "д.")

        setIfNotExists("formats.time-format.default.config.onlySeconds", "%seconds%%seconds_placeholder%")
        setIfNotExists("formats.time-format.default.config.onlyMinutes", "%minutes%%minutes_placeholder%")
        setIfNotExists("formats.time-format.default.config.onlyHours", "%hours%%hours_placeholder%")
        setIfNotExists("formats.time-format.default.config.onlyDays", "%days%%days_placeholder%")
        setIfNotExists("formats.time-format.default.config.secondsMinutes", "%minutes%%minutes_placeholder% %seconds%%seconds_placeholder%")
        setIfNotExists("formats.time-format.default.config.secondsHours", "%hours%%hours_placeholder% %seconds%%seconds_placeholder%")
        setIfNotExists("formats.time-format.default.config.secondsDays", "%days%%days_placeholder% %seconds%%seconds_placeholder%")
        setIfNotExists("formats.time-format.default.config.secondsMinutesHours", "%hours%%hours_placeholder% %minutes%%minutes_placeholder% %seconds%%seconds_placeholder%")
        setIfNotExists("formats.time-format.default.config.secondsHoursDays", "%days%%days_placeholder% %hours%%hours_placeholder% %seconds%%seconds_placeholder%")
        setIfNotExists("formats.time-format.default.config.secondsMinutesDays", "%days%%days_placeholder% %minutes%%minutes_placeholder% %seconds%%seconds_placeholder%")
        setIfNotExists("formats.time-format.default.config.secondsMinutesHoursDays", "%days%%days_placeholder% %hours%%hours_placeholder% %minutes%%minutes_placeholder% %seconds%%seconds_placeholder%")
        setIfNotExists("formats.time-format.default.config.minutesHours", "%hours%%hours_placeholder% %minutes%%minutes_placeholder%")
        setIfNotExists("formats.time-format.default.config.minutesDays", "%days%%days_placeholder% %minutes%%minutes_placeholder%")
        setIfNotExists("formats.time-format.default.config.minutesHoursDays", "%days%%days_placeholder% %hours%%hours_placeholder% %minutes%%minutes_placeholder%")
        setIfNotExists("formats.time-format.default.config.hoursDays", "%days%%days_placeholder% %hours%%hours_placeholder%")
    }

    val timeManager: TimeManager = TimeManager(config)

    fun timeManagerDefaultFormat(millis: Long): String = timeManager.parseTime("formats.time-format.default", millis)
    fun timeManagerFormat(prefix: String, millis: Long): String = timeManager.parseTime("formats.time-format.$prefix", millis)
}