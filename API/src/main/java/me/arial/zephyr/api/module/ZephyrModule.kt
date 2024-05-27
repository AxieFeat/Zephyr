package me.arial.zephyr.api.module

import me.arial.zephyr.api.ZephyrPlugin
import me.arial.zephyr.api.module.command.ZephyrModuleCommandManager
import org.bukkit.Bukkit
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.event.HandlerList
import org.bukkit.event.Listener
import java.io.*
import java.util.jar.JarFile
import java.util.logging.Level
import java.util.logging.Logger
import java.util.zip.ZipEntry

/**
 * Объект модуля Zephyr
 */
abstract class ZephyrModule {

    init {
        if (this.javaClass.classLoader !is ModuleClassLoader) {
            throw IllegalArgumentException("ZephyrModule требует ${ModuleClassLoader::class.java.name}")
        }
    }

    abstract fun onEnable()
    abstract fun onDisable()
    abstract fun onReload()

    /**
     * Статус модуля
     */
    var enabled = true

    /**
     * Аннотация [ModuleInfo] от текущего класса
     */
    lateinit var info: ModuleInfo

    /**
     * Список зарегистрированных слушателей
     */
    val registeredListeners = mutableListOf<Any>()

    /**
     * Получение [ZephyrModuleManager]
     */
    val moduleManager = ZephyrPlugin.instance.zephyrModuleManager

    /**
     * Получение [ZephyrCommandManager]
     */
    val commandManager: ZephyrModuleCommandManager = ZephyrPlugin.instance.createModuleCommandManagerInstanceFor(this)

//    /**
//     * Клиент KryoNet
//     */
//    val client = ZephyrPlugin.instance.kryoClient!!

    /**
     * Объект [Logger] модуля
     */
    lateinit var logger: Logger

    /**
     * Папка модуля
     */
    lateinit var dataFolder: File

    /**
     * Файкл конфигурации модуля
     */
    lateinit var configFile: File

    /**
     * Конфигурация модуля
     */
    lateinit var config: FileConfiguration

    /**
     * Файл модуля
     */
    lateinit var moduleFile: File

    /**
     * Получение [InputStream] файла из файла модуля
     *
     * @param filename Путь до файла внутри модуля
     * @return [InputStream] файла
     */
    fun getResource(filename: String): InputStream? {
        try {
            val jarFile = JarFile(moduleFile)
            val entry: ZipEntry = jarFile.getEntry(filename)

            return jarFile.getInputStream(entry)
        } catch (e: IOException) {
            return null
        }
    }

    /**
     * Сохраняет файл из файла модуля в папку модуля.
     * При сохранении сохраняет путь из path
     *
     * @param path Путь до файла внутри модуля
     * @param replace Перезаписывать файл, если существует
     */
    fun saveResource(path: String, replace: Boolean) {
        if (path != "") {
            val resourcePath = path.replace('\\', '/')
            val `in`: InputStream = this.getResource(resourcePath)!!

            val outFile = File(this.dataFolder, resourcePath)
            val lastIndex: Int = resourcePath.lastIndexOf(47.toChar())
            val outDir = File(this.dataFolder, resourcePath.substring(0, if (lastIndex >= 0) lastIndex else 0))
            if (!outDir.exists()) {
                outDir.mkdirs()
            }

            try {
                if (outFile.exists() && !replace) {
                    logger.log(
                        Level.WARNING,
                        "Невозможно сохранить ${outFile.name} в $outFile, потому что ${outFile.name} уже существует!"
                    )
                } else {
                    val out: OutputStream = FileOutputStream(outFile)
                    val buf = ByteArray(1024)

                    var len: Int
                    while ((`in`.read(buf).also { len = it }) > 0) {
                        out.write(buf, 0, len)
                    }

                    out.close()
                    `in`.close()
                }
            } catch (e: IOException) {
                logger.log(Level.SEVERE, "Невозможно сохранить ${outFile.name} в $outFile", e)
            }
        } else {
            throw IllegalArgumentException("Путь не может быть пустым!")
        }
    }

    /**
     * Сохраняет файл config.yml из модуля в его папку
     */
    fun saveDefaultConfig() {
        if (!configFile.exists()) {
            this.saveResource("config.yml", false)
            this.config = YamlConfiguration.loadConfiguration(File(dataFolder, "config.yml"))
        }
    }

    /**
     * Перезагружает конфигурацию модуля
     */
    fun reloadConfig() {
        config.load(File(dataFolder.path, "config.yml"))
    }

    /**
     * Регистрирует слушатель событий
     *
     * @param listener Объект слушателя
     */
    fun registerListener(listener: Any) {
        Bukkit.getPluginManager().registerEvents(listener as Listener, ZephyrPlugin.instance)
        registeredListeners.add(listener)
//        Packet(
//            type = PacketType.LISTENER_REGISTER,
//            content = arrayOf(
//                info.name,
//                info.version,
//                info.reloadable,
//                info.authors,
//                listener::class.java.name
//            )
//        ).send()
    }

    /**
     * Отмена регистрации слушателя событий
     *
     * @param listener Объект слушателя
     */
    fun unregisterListener(listener: Any) {
        HandlerList.unregisterAll(listener as Listener)
        registeredListeners.remove(listener)
//
//        Packet(
//            type = PacketType.LISTENER_UNREGISTER,
//            content = arrayOf(
//                info.name,
//                info.version,
//                info.reloadable,
//                info.authors,
//                listener::class.java.name
//            )
//        ).send()
    }
}