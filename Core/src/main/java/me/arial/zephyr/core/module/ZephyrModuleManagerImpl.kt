package me.arial.zephyr.core.module

import me.arial.zephyr.api.event.module.ModuleLoadEvent
import me.arial.zephyr.api.event.module.ModuleReloadEvent
import me.arial.zephyr.api.event.module.ModuleUnloadEvent
import me.arial.zephyr.api.module.*
import me.arial.zephyr.core.Zephyr
import org.bukkit.Bukkit
import java.io.File
import java.util.logging.Level


class ZephyrModuleManagerImpl(
    override val moduleFolder: File
) : ZephyrModuleManager {

    init {
        if (!moduleFolder.exists()) {
            moduleFolder.mkdirs()
        }
    }

    private val loadedModules = mutableMapOf<String, LoadedModule>()

    override fun loadModule(file: File, withEvent: Boolean): LoadedModule? {
        val loader = ModuleClassLoader(file)

        try {
            val result = loader.load()

            if (result != null) {

                if (withEvent) {
                    Bukkit.getScheduler().runTaskAsynchronously(Zephyr.instance, Runnable {
                        val moduleLoadEvent = ModuleLoadEvent(result)
                        Bukkit.getPluginManager().callEvent(moduleLoadEvent)
                    })
                }

                loadedModules[result.name] = result
                return result
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    override fun loadModules(): Boolean {
        val files: Array<out File> = moduleFolder.listFiles { _: File?, name: String -> name.endsWith(".jar") } ?: return false

        var success = 0
        var fail = 0

        for (file in files) {

            val loadedModule = loadModule(file)

            if (loadedModule != null) {
                Zephyr.instance.logger.log(Level.INFO, "Загружен модуль ${loadedModule.name} v${loadedModule.version}")
                success++
            } else {
                Zephyr.instance.logger.log(Level.INFO, "Произошла ошибка при загрузке модуля \"${file.name}\"!")
                fail++
            }
        }

        Zephyr.instance.logger.log(
            Level.INFO,
            "Загружено $success модулей. (Пропущено $fail)"
        )

        return true
    }

    override fun loadedModules(): Map<String, LoadedModule> {
        return loadedModules
    }

    override fun unloadModule(loadedModule: LoadedModule?, withEvent: Boolean): Boolean {
        if (loadedModule == null) {
            return false
        }

        if (withEvent) {
            Bukkit.getScheduler().runTaskAsynchronously(Zephyr.instance, Runnable {
                val moduleUnloadEvent = ModuleUnloadEvent(loadedModule)
                Bukkit.getPluginManager().callEvent(moduleUnloadEvent)
            })
        }


        (loadedModule.module.javaClass.classLoader as ModuleClassLoader).unload()
        loadedModules.remove(loadedModule.name)

        Zephyr.instance.logger.log(Level.INFO, "Выгружен модуль ${loadedModule.name} v${loadedModule.version}")

        return true
    }

    override fun unloadModule(module: ZephyrModule, withEvent: Boolean): Boolean {
        loadedModules.forEach {
            if (it.value.module == module) {
                unloadModule(it.value, withEvent)
                return true
            }
        }

        return false
    }

    override fun reloadModule(loadedModule: LoadedModule, withEvent: Boolean): Boolean {
        if (!loadedModules.containsKey(loadedModule.name)) return false
        if (!loadedModule.reloadable) return false

        if (withEvent) {
            Bukkit.getScheduler().runTaskAsynchronously(Zephyr.instance, Runnable {
                val moduleReloadEvent = ModuleReloadEvent(loadedModule)
                Bukkit.getPluginManager().callEvent(moduleReloadEvent)
            })
        }

//        Packet(
//            type = PacketType.MODULE_RELOAD, content = arrayOf(
//                loadedModule.name,
//                loadedModule.version,
//                loadedModule.reloadable,
//                loadedModule.authors,
//                loadedModule.module.moduleFile.getChecksum() ?: ""
//            )
//        ).send()

        loadedModule.module.onReload()

        Zephyr.instance.logger.log(Level.INFO, "Перезагружен модуль ${loadedModule.name} v${loadedModule.version}")

        return true
    }

    override fun reloadModule(module: ZephyrModule, withEvent: Boolean): Boolean {
        loadedModules.forEach {
            if (it.value.module == module) {
                reloadModule(it.value, withEvent)
                return true
            }
        }

        return false
    }

    override fun getModule(name: String?): LoadedModule? {
        return loadedModules[name]
    }
}