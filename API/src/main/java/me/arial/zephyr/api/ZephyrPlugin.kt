package me.arial.zephyr.api

import me.arial.zephyr.api.command.ZephyrCommandManager
import me.arial.zephyr.api.module.ZephyrModule
import me.arial.zephyr.api.module.ZephyrModuleManager
import me.arial.zephyr.api.module.command.ZephyrModuleCommandManager
import org.bukkit.plugin.java.JavaPlugin

/**
 * Префикс Zephyr, используется в командах
 */
const val prefix = "&7[&#FB0824Z&#FA0A32e&#F90C3Fp&#F80F4Dh&#F7115Ay&#F61368r&7]&f"

abstract class ZephyrPlugin : JavaPlugin() {

    /**
     * Версия Zephyr
     */
    val zephyrVersion = "1.3-BETA5"
//    val session = UUID.randomUUID()
//    val hwid = info()
//    val hash = jarFile.getChecksum() ?: ""

    companion object {
        lateinit var instance: ZephyrPlugin
    }

    init {
        instance = this
    }

   // abstract var key: String
    /**
     * Объект [ZephyrCommandManager]
     */
    abstract var zephyrCommandManager: ZephyrCommandManager

    /**
     * Объект [ZephyrModuleManager]
     */
    abstract var zephyrModuleManager: ZephyrModuleManager
    //abstract var kryoClient: KryoClient

    protected abstract fun enable()
    protected abstract fun disable()
    protected abstract fun reload()

    abstract fun stop(reason: String)

    abstract val registeredModuleCommands: MutableList<Pair<ZephyrModule, Any>>
    abstract fun createModuleCommandManagerInstanceFor(module: ZephyrModule): ZephyrModuleCommandManager
    abstract fun getZephyrModuleCommandManager(module: ZephyrModule): ZephyrModuleCommandManager?
    abstract fun removeZephyrModuleCommandManager(module: ZephyrModule)

    override fun onEnable() {
        enable()

//        if (!kryoClient.client!!.isConnected) {
//            Bukkit.getPluginManager().disablePlugin(this)
//        }
    }

    override fun onDisable() {
        disable()
    }
}