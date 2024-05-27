package me.arial.zephyr.api.module

import me.arial.zephyr.api.Extension.Companion.startWith
import me.arial.zephyr.api.ZephyrPlugin
import me.arial.zephyr.api.module.exception.ModuleInitException
import org.bukkit.Bukkit
import org.bukkit.configuration.file.YamlConfiguration
import java.io.File
import java.net.URLClassLoader
import java.util.jar.JarEntry
import java.util.jar.JarFile
import kotlin.jvm.Throws
import kotlin.reflect.KClass
import kotlin.reflect.full.createInstance
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.hasAnnotation
import kotlin.reflect.full.isSubclassOf

/**
 * ClassLoader модулей
 */
class ModuleClassLoader(
    private val file: File
) : URLClassLoader(
    arrayOf(file.toURI().toURL()),
    ZephyrPlugin::class.java.classLoader
) {

    var loadedModule: LoadedModule? = null

    @Throws(ModuleInitException::class)
    fun load(): LoadedModule? {
        val jarFile = JarFile(this.file)
        val entries: MutableList<JarEntry> = ArrayList()
        jarFile.stream().forEach { e: JarEntry ->
            entries.add(
                e
            )
        }

        jarFile.close()

        for (entry in entries) {
            var name = entry.name

            if (name.endsWith(".class")) {

                name = name.substring(0, name.length - 6).replace('/', '.')

                if (!name.startWith("kotlin", "org.jetbrains", "org.intellij", "META-INF")) {
                    try {
                        val clazz = this.loadClass(name).kotlin

                        val result = initClass(file, clazz)

                        if (result != null) {
                            loadedModule = result
                        }
                    } catch (ignore: ClassNotFoundException) {
                    } catch (ignore: NoClassDefFoundError) {
                    }
                }
            }
        }

        this.close()

        return loadedModule
    }

    fun initClass(moduleFile: File, clazz: KClass<out Any>): LoadedModule? {
        if (!moduleFile.exists()) {
            throw ModuleInitException("Файл модуля не найден!")
        }
        if (ZephyrPlugin.instance.description.name != "Zephyr") {
            throw ModuleInitException("Ошибка при инициализации модуля!")
        }
        if (!clazz.hasAnnotation<ModuleInfo>() || !clazz.isSubclassOf(ZephyrModule::class)) {
            return null
        }

        val module: ZephyrModule = clazz.createInstance() as ZephyrModule

        val info = clazz.findAnnotation<ModuleInfo>()!!

        return initModule(module, info, moduleFile)
    }

    fun initModule(module: ZephyrModule, info: ModuleInfo, moduleFile: File): LoadedModule {
        val loadedModules = ZephyrPlugin.instance.zephyrModuleManager.loadedModules()

        info.depend.forEach {
            if (!loadedModules.containsKey(it) && Bukkit.getPluginManager().getPlugin(it) == null) {
                throw ModuleInitException("Модуль ${info.name} имеет зависимость $it, установите её!")
            }
        }

        module.moduleFile = moduleFile
        module.logger = ZephyrLogger.getLogger(info.name)
        module.dataFolder = File(this.file.parentFile.path + File.separator + info.name)
        module.configFile = File(module.dataFolder, "config.yml")
        module.config = YamlConfiguration.loadConfiguration(module.configFile)
        module.info = info
        module.enabled = true

//        Packet(type = PacketType.MODULE_LOAD, content = arrayOf(
//            info.name,
//            info.version,
//            info.reloadable,
//            info.authors,
//            module.moduleFile.getChecksum() ?: ""
//        )).send()

        module.onEnable()

        return LoadedModule(
            module,
            info.name,
            info.version,
            info.reloadable,
            info.authors,
            info.description,
            info.website,
            info.depend,
            info.soft
        )
    }

    fun unload() {
        if (loadedModule == null) {
            return
        }

//        Packet(type = PacketType.MODULE_UNLOAD, content = arrayOf(
//            loadedModule!!.name,
//            loadedModule!!.version,
//            loadedModule!!.reloadable,
//            loadedModule!!.authors,
//            loadedModule!!.module.moduleFile.getChecksum() ?: ""
//        )).send()

        loadedModule!!.module.onDisable()
        loadedModule!!.module.enabled = false

        loadedModule!!.module.registeredListeners.toList().forEach {
            loadedModule!!.module.unregisterListener(it)
        }

        ZephyrPlugin.instance.getZephyrModuleCommandManager(
            loadedModule!!.module
        )?.unregisterCommands()

        ZephyrPlugin.instance.removeZephyrModuleCommandManager(loadedModule!!.module)
    }

}