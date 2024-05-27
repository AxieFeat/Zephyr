package me.arial.zephyr.api

import me.arial.zephyr.api.item.ItemBuilder
import me.arial.zephyr.api.text.ColorParser
import me.arial.zephyr.api.text.LangComponent
import net.kyori.adventure.platform.bukkit.BukkitAudiences
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.TextReplacementConfig
import net.kyori.adventure.text.minimessage.MiniMessage
import net.md_5.bungee.api.ChatColor
import org.bukkit.Sound
import org.bukkit.command.CommandSender
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.scheduler.BukkitRunnable
import org.bukkit.scheduler.BukkitScheduler
import org.bukkit.util.io.BukkitObjectOutputStream
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder
import java.io.*
import java.net.URISyntaxException
import java.security.MessageDigest
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.Callable
import java.util.regex.Pattern

/**
 * Класс с разными утилитами
 */
class Extension {

    companion object {

        private val miniMessage = MiniMessage.builder().build()
        private val bukkitAudiences = BukkitAudiences.builder(ZephyrPlugin.instance).build()

        /**
         * Отправка компонента игроку через [BukkitAudiences]
         *
         * @param component Компонент для отправки
         */
        fun Player.sendNativeComponent(component: Component) {
            bukkitAudiences.player(this).sendMessage(component)
        }

        /**
         * Отправляет [LangComponent] игроку
         *
         * @param langComponent Компонент для отправки
         */
        fun Player.sendLangComponent(langComponent: LangComponent) {
            if (player == null) return

            if (langComponent.components != null) {
                langComponent.components!!.forEach {
                    player!!.sendNativeComponent(it)
                }
            }

            if (langComponent.wrappedSound != null)
            langComponent.wrappedSound!!.play(this)

            var components = ""
            langComponent.components?.forEach {
                components += miniMessage.serialize(it).cut(150) + "\n"
            }

//            Packet(
//                type = PacketType.MESSAGE,
//                content = arrayOf(
//                    this.name, components
//                )
//            ).send()
        }

        /**
         * Отправляет сообщение с парсингом цветов
         *
         * @param text Сообщение, которое необходимо отправить
         */
        fun Player.sendColoredMessage(text: String) {
            sendMessage(ColorParser.parseColor(text))

//            Packet(
//                type = PacketType.MESSAGE,
//                content = arrayOf(
//                    this.name, text.cut(150)
//                )
//            ).send()
        }

        /**
         * Отправляет сообщения с парсингом цветов
         *
         * @param text Сообщения, которые необходимо отправить
         */
        fun Player.sendColoredMessage(text: List<String>) = text.forEach {
            sendMessage(
                ColorParser.parseColor(it)
            )

//            Packet(
//                type = PacketType.MESSAGE,
//                content = arrayOf(
//                    this.name, text.toString().cut(150)
//                )
//            ).send()
        }

        /**
         * Отправляет сообщение с парсингом цветов
         *
         * @param text Сообщение, которое необходимо отправить
         */
        fun CommandSender.sendColoredMessage(text: String) {
            sendMessage(ColorParser.parseColor(text))

//            Packet(
//                type = PacketType.MESSAGE,
//                content = arrayOf(
//                    this.name, text.cut(150)
//                )
//            ).send()
        }

        /**
         * Отправляет сообщения с парсингом цветов
         *
         * @param text Сообщения, которые необходимо отправить
         */
        fun CommandSender.sendColoredMessage(text: List<String>) = text.forEach {
            sendMessage(
                ColorParser.parseColor(it)
            )

//            Packet(
//                type = PacketType.MESSAGE,
//                content = arrayOf(
//                    this.name, text.toString().cut(150)
//                )
//            ).send()
        }

        /**
         * Возвращает из мапы значение по шансам в
         * её ключах, при этом сумма всех ключей должна быть равна 100
         *
         * @return Значение из мапы по шансам из ключей
         */
        fun <V> Map<Int, V>.selectValueByChance(): V? {
            val totalChances = keys.sum()
            if (totalChances != 100) {
                throw IllegalArgumentException("Сумма всех шансов должна быть равна 100")
            }

            val randomChance = (1..100).random()
            var currentChance = 0

            for ((chance, value) in this) {
                currentChance += chance
                if (randomChance <= currentChance) {
                    return value
                }
            }
            return null
        }

        /**
         * Заменяет текст в Component
         *
         * @param old Что нужно заменить
         * @param new Строка для замены
         *
         * @return Изменённый Component
         */
        fun Component.replace(old: String, new: String): Component {
            return replaceText(
                TextReplacementConfig.builder().match(
                    Pattern.compile(old)
                ).replacement(new).build()
            )
        }

        /**
         * Заменяет текст в [Component] с парсингом MiniMessage
         *
         * @param old Что нужно заменить
         * @param new Строка для замены
         *
         * @return Изменённый Component
         */
        fun Component.replaceWithParse(old: String, new: String): Component {
            return replaceText(
                TextReplacementConfig.builder().match(
                    Pattern.compile(old)
                ).replacement(miniMessage.deserialize(new)).build()
            )
        }

        /**
         * Случайный ключ из мапы по условию
         *
         * @param predicate Условия для рандомного ключа
         *
         * @return Случайный ключ по условию
         */
        fun <K, V> Map<K, V>.randomKey(predicate: (V) -> Boolean): K? {
            return this.filter { predicate(it.value) }.keys.randomOrNull()
        }

        /**
         * Получить ключ из мапы по значение
         *
         * @param value Значение, из которого нужно получить ключ
         *
         * @return Ключ по значению
         */
        fun <T, E> Map<T, E>.getKeyByValue(value: E?): T? {
            for ((key, value1) in toMap()) {
                if (value == value1) {
                    return key
                }
            }
            return null
        }

        /**
         * Парсит аргументы в строке.
         * функция заменяет {ЧИСЛО} на элемемент
         * из массива по его номеру начиная с единицы
         *
         * Например "Сейчас {1}".parse("солнечно")
         *
         * @param args Элементы для замены
         * @return Изменённая строка
         */
        fun String.parse(vararg args: Any): String {
            val sb = StringBuilder()

            val pattern = Pattern.compile("\\{(\\d+)}")
            val matcher = pattern.matcher(this)

            var pos = 0

            while (matcher.find()) {
                sb.append(this.substring(pos, matcher.start()))

                val index = matcher.group(1).toInt()

                if (index > 0 && index <= args.size) {
                    sb.append(args[index - 1])
                }

                pos = matcher.end()
            }

            sb.append(this.substring(pos))

            return sb.toString()
        }

        /**
         * Выводит объект в консоль
         */
        fun <T> T.print(): T {
            println(this)

            return this
        }

        /**
         * .jar файл плагина
         *
         * @return Объект [File] .jar файла плагина
         */
        val JavaPlugin.jarFile: File
            get() {
                try {
                    val path = this::class.java.protectionDomain.codeSource.location.toURI().path

                    return File(dataFolder.parentFile.path + "/" + path.split("/").last())
                } catch (e: URISyntaxException) {
                    e.printStackTrace()
                }

                return File("")
            }

        /**
         * Начинается ли строка с одной из указанных строк
         *
         * @param values Строки для проверки
         * @return true, если строка начинается с одного из элементов, и false, если нет
         */
        fun String.startWith(vararg values: String, ignoreCase: Boolean = false): Boolean {
            values.forEach {
                if (this.startsWith(it, ignoreCase)) return true
            }

            return false
        }

        /**
         * Заканчивается ли строка одной из указанных строк
         *
         * @param values Строки для проверки
         * @return true, если строка заканчивается одним из эелементов, и false, если нет
         */
        fun String.endWith(vararg values: String, ignoreCase: Boolean = false): Boolean {
            values.forEach {
                if (this.endsWith(it, ignoreCase)) return true
            }

            return false
        }

//        /**
//         * Представляет объект в виде пакета
//         *
//         * @return Объект пакета
//         */
//        fun Any.asPacket(type: PacketType): Packet {
//            return Packet(type = type, content = arrayOf(this))
//        }

        /**
         * Сериализует [ItemStack] в base64 строку
         *
         * @return base64 строка
         */
        val ItemStack.base64: String
            get() {
                try {
                    ByteArrayOutputStream().use { outputStream ->
                        BukkitObjectOutputStream(outputStream).use { dataOutput ->
                            val array = arrayOf(this)

                            dataOutput.writeInt(array.size)
                            for (item in array) dataOutput.writeObject(item)
                            return Base64Coder.encodeLines(outputStream.toByteArray())
                        }
                    }
                } catch (ignored: Exception) {
                    return ""
                }
            }

        /**
         * Объект [LangComponent] из конфигурации
         *
         * @param path Путь в конфиге
         * @return Объект [LangComponent]
         */
        fun FileConfiguration.getLangComponent(path: String?): LangComponent {
            return LangComponent.deserialize(this, path)
        }

        /**
         * Объект [ItemBuilder] из секции конфигурации
         *
         * @param parseColor Парсить ли цвета
         *
         * @return Объект [ItemBuilder]
         */
        fun ConfigurationSection.getItemBuilder(parseColor: Boolean = true): ItemBuilder {
            return ItemBuilder.fromConfig(this, parseColor)
        }

        /**
         * Объект [ItemBuilder] из конфигурации
         *
         * @param path Путь в конфиге
         * @param parseColor Парсить ли цвета
         *
         * @return Объект [ItemBuilder]
         */
        fun FileConfiguration.getItemBuilder(path: String, parseColor: Boolean = true): ItemBuilder {
            return ItemBuilder.fromConfig(this.getConfigurationSection(path)!!, parseColor)
        }

        /**
         * Объединяет элементы массива, если они содержат " или '
         *
         * @return Изменённый массив
         */
        fun Array<out String>?.parseArgs(): Array<out String>? {
            if (this == null) return null

            val result = mutableListOf<String>()
            var insideQuotes = false
            var currentString = ""

            for (element in this) {
                if (element.startsWith("\"") || element.startsWith("'")) {
                    val cleanedElement = element.removePrefix("\"").removePrefix("'")

                    if (cleanedElement.endsWith("\"") || cleanedElement.endsWith("'")) {
                        val finalElement = cleanedElement.removeSuffix("\"").removeSuffix("'")
                        if (finalElement.isNotEmpty()) {
                            result.add(finalElement)
                        }
                    } else {
                        currentString += cleanedElement
                        insideQuotes = true
                    }
                } else if (insideQuotes) {

                    if (element.endsWith("\"") || element.endsWith("'")) {
                        val finalElement = element.removeSuffix("\"").removeSuffix("'")
                        if (finalElement.isNotEmpty()) {
                            currentString += " $finalElement"
                            result.add(currentString)
                            currentString = ""
                            insideQuotes = false
                        }
                    } else {
                        currentString += " $element"
                    }

                } else {
                    result.add(element)
                }
            }

            return result.toTypedArray()
        }

        /**
         * Хеширует строку в MD5
         *
         * @return MD5 хеш
         */
        fun String.toMd5(): String {
            val md = MessageDigest.getInstance("MD5")
            md.update(this.toByteArray())

            val hexString = StringBuffer()

            val byteData = md.digest()

            for (aByteData in byteData) {
                val hex = Integer.toHexString(0xff and aByteData.toInt())
                if (hex.length == 1) hexString.append('0')
                hexString.append(hex)
            }

            return hexString.toString()
        }

        /**
         * Форматирует [Long] в дату
         *
         * @param format Формат [SimpleDateFormat]
         * @return Форматированное время
         */
        fun Long.toFormattedTime(format: String = "dd.MM.yyyy HH:mm:ss"): String {
            val date = Date(this)

            val sdf = SimpleDateFormat(format)
            return sdf.format(date)
        }

        /**
         * @return Хеш-сумма файла
         */
        fun File.getChecksum(): String? {
            try {
                val digest = MessageDigest.getInstance("SHA-256")
                val fis = FileInputStream(this)
                val byteArray = ByteArray(1024)
                var bytesCount: Int
                while ((fis.read(byteArray).also { bytesCount = it }) != -1) {
                    digest.update(byteArray, 0, bytesCount)
                }
                fis.close()
                val bytes = digest.digest()
                val sb = StringBuilder()
                for (b in bytes) {
                    sb.append(((b.toInt() and 0xff) + 0x100).toString(16).substring(1))
                }
                return sb.toString()
            } catch (e: Exception) {
                e.printStackTrace()
                return null
            }
        }

        /**
         * Парсит HEX цвет в строке
         *
         * @return Изменённая строка
         */
        fun String.parseColor(): String {
            return ColorParser.parseColor(this)
        }

        /**
         * Парсит &#RRGGBB в формат MiniMessage
         *
         * @return Изменённая строка
         */
        fun String.parseMiniMessage(): String {
            val matcher = Pattern.compile("(&#[0-9a-fA-F]{6})").matcher(this)
            val sb = StringBuilder()

            while (matcher.find()) {
                val hex = matcher.group(1).substring(1)
                matcher.appendReplacement(sb, "<color:$hex>")
            }

            matcher.appendTail(sb)
            val hexColored = sb.toString()

            return ChatColor.translateAlternateColorCodes('&', hexColored)
        }

        /**
         * Удаляет все элементы в массиве после указанного индекса
         *
         * @param index Индекс
         * @return Изменённый массив
         */
        fun <T> Array<T>.removeElementsAfterIndex(index: Int): Array<T> {
            if (index >= this.size) {
                return this
            }

            return this.copyOfRange(0, index + 1)
        }

        /**
         * Удаляет все элементы в мапе после указанного индекса
         *
         * @param index Индекс
         * @return Изменённая мапа
         */
        fun <K, V> MutableMap<K, V>.removeElementsAfterIndex(index: Int): MutableMap<K, V> {
            if (index >= this.size) {
                return this
            }

            val result = this.toMutableMap()

            val keysToRemove = this.keys.drop(index + 1)
            keysToRemove.forEach { key ->
                result.remove(key)
            }

            return result
        }

        /**
         * Обрезает строку до указанной длины
         *
         * @param length Длина
         * @return Изменённая строка
         */
        fun String.cut(length: Int): String {
            return if (this.length <= length) {
                this
            } else {
                this.substring(0, length)
            }
        }

        /**
         * Преобразует строку в звук из класса [Sound]
         *
         * @return звук
         */
        fun String.toBukkitSound(): Sound {
            return Sound.valueOf(this)
        }

        /**
         * Получение HWID устройства
         */
        val hwid: String
            get() {
                val list = mutableListOf<String>()

                if (System.getProperty("os.name").lowercase().contains("win")) {
                    val process = Runtime.getRuntime().exec("wmic diskdrive get serialnumber")
                    val reader = process.inputStream.bufferedReader()
                    reader.lines().forEach { line ->
                        if (line.isNotBlank() && line.trim() != "SerialNumber") {
                            list.add(line.trim())
                        }
                    }
                } else {
                    val disks = File("/dev/disk/by-uuid/").listFiles()
                    disks?.forEach { disk ->
                        list.add(disk.name)
                    }
                }

                if (System.getProperty("os.name").lowercase().contains("win")) {
                    val memoryProcess = Runtime.getRuntime().exec("wmic memorychip get capacity")
                    val memoryReader = BufferedReader(InputStreamReader(memoryProcess.inputStream))
                    val memoryLines = memoryReader.readLines()
                    val totalMemoryBytes = memoryLines
                        .filter { it.isNotBlank() }
                        .filter { it.trim() != "Capacity" }


                    list.add(totalMemoryBytes.toString())

                    val processorProcess = Runtime.getRuntime().exec("wmic cpu get name, processorid")
                    val processorReader = BufferedReader(InputStreamReader(processorProcess.inputStream))
                    val processorLines = processorReader.readLines()

                    list.add(
                        processorLines.filter { it.isNotBlank() && it.trim() != "Name" && it.trim() != "ProcessorId" }.toString()
                            .replace(" ", "")
                    )
                } else {
                    val memoryProcess = Runtime.getRuntime().exec("free -m")
                    val memoryReader = BufferedReader(InputStreamReader(memoryProcess.inputStream))
                    val memoryLines = memoryReader.readLines()
                    val totalMemoryLine = memoryLines.find { it.startsWith("Mem:") }
                    val totalMemory = totalMemoryLine?.split("\\s+".toRegex())?.get(1) ?: "Unknown"

                    list.add(totalMemory)
                }

                return list.toString().toMd5()
            }

        /**
         * Заменяет в названии и лоре объекта [ItemStack] указанные значения
         *
         * @param old Старое значение
         * @param new Новое значение
         *
         * @return Текущий объект [ItemStack]
         */
        fun ItemStack.replace(old: String, new: String): ItemStack {
            val meta = this.itemMeta

            if (meta.hasDisplayName()) {
                meta.displayName(
                    meta.displayName()!!.replace(old, new)
                )
            }
            if (meta.hasLore()) {
                val newLore = mutableListOf<Component>()

                meta.lore()!!.forEach {
                    newLore.add(it.replace(old, new))
                }

                meta.lore(newLore)
            }

            this.itemMeta = meta

            return this
        }

        /**
         * Получение мапы из конфига
         *
         * @param path Путь в конфиге
         *
         * @return Мапа из конфига, ключ - ключ в конфигурации, значение - значение в конфигурации
         */
        fun FileConfiguration.getMap(path: String): Map<String, Any?> {
            val result = mutableMapOf<String, Any?>()

            this.getConfigurationSection(path)?.getKeys(false)?.forEach {
                result[it] = this.get("$path.$it")
            }

            return result
        }

        /**
         * Получение мапы из секции
         *
         * @param path Путь в конфиге
         *
         * @return Мапа из конфига, ключ - ключ в секции, значение - значение в секции
         */
        fun ConfigurationSection?.getMap(path: String): Map<String, Any?> {
            val result = mutableMapOf<String, Any?>()

            this?.getConfigurationSection(path)?.getKeys(false)?.forEach {
                result[it] = this.get("$path.$it")
            }

            return result
        }

        private val waitingTasks = mutableMapOf<Int, BukkitRunnable>()

        /**
         * Очищает все запланированные задачи от методов [scheduleTask] и [scheduleAsynchronouslyTask]
         */
        fun BukkitScheduler.clearScheduledTasks() {
            waitingTasks.clear()
        }

        /**
         * Отменяет выполнение задачи по указанному номеру
         *
         * @param num Номер задачи
         */
        fun BukkitScheduler.cancelScheduledTask(num: Int) {
            waitingTasks.remove(num)
        }

        /**
         * Выполняет задачу в указанное время в указанном формате
         *
         * @param dateFormat Формат даты, по умолчанию ДЕНЬ;ЧАС:МИНУТА:СЕКУНДА
         * @param dateString Дата в формате ДЕНЬ;ЧАС:МИНУТА:СЕКУНДА
         * @param task Задача, которая должна быть выполнена
         *
         * @return Порядковые номер задачи, -1 если задача не была запущена
         */
        fun BukkitScheduler.scheduleTask(dateFormat: String = "EEEE;HH:mm:ss", dateString: String, task: Callable<Unit>): Int {
            val sdf = SimpleDateFormat(dateFormat)
            val targetDate = sdf.parse(dateString)

            val currentTime = System.currentTimeMillis()
            val ticksUntilDate = (targetDate.time - currentTime) / 50

            if (ticksUntilDate < 0) {
                return -1
            }

            val taskNum = waitingTasks.size

            val runnable = object : BukkitRunnable() {
                override fun run() {
                    if(waitingTasks.containsKey(taskNum)) {
                        task.call()
                        waitingTasks.remove(taskNum)
                    }
                }
            }

            waitingTasks[waitingTasks.size] = runnable

            runnable.runTaskLater(ZephyrPlugin.instance, ticksUntilDate)

            return taskNum
        }

        /**
         * Выполняет асинхронную задачу в указанное время в указанном формате
         *
         * @param dateFormat Формат даты, по умолчанию ДЕНЬ;ЧАС:МИНУТА:СЕКУНДА
         * @param dateString Дата в формате ДЕНЬ;ЧАС:МИНУТА:СЕКУНДА
         * @param task Задача, которая должна быть выполнена
         *
         * @return Порядковые номер задачи, -1 если задача не была запущена
         */
        fun BukkitScheduler.scheduleAsynchronouslyTask(dateFormat: String = "EEEE;HH:mm:ss", dateString: String, task: Callable<Unit>): Int {
            val sdf = SimpleDateFormat(dateFormat)
            val targetDate = sdf.parse(dateString)

            val currentTime = System.currentTimeMillis()
            val ticksUntilDate = (targetDate.time - currentTime) / 50

            if (ticksUntilDate < 0) {
                return -1
            }

            val taskNum = waitingTasks.size

            val runnable = object : BukkitRunnable() {
                override fun run() {
                    if(waitingTasks.containsKey(taskNum)) {
                        task.call()
                        waitingTasks.remove(taskNum)
                    }
                }
            }

            waitingTasks[waitingTasks.size] = runnable

            runnable.runTaskLaterAsynchronously(ZephyrPlugin.instance, ticksUntilDate)

            return taskNum
        }
    }
}