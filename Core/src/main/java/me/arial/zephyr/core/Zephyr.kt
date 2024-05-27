package me.arial.zephyr.core

import com.comphenix.protocol.PacketType
import com.comphenix.protocol.ProtocolLibrary
import com.comphenix.protocol.events.ListenerPriority
import com.comphenix.protocol.events.PacketAdapter
import com.comphenix.protocol.events.PacketEvent
import com.comphenix.protocol.wrappers.WrappedChatComponent
import dev.jorel.commandapi.CommandAPI
import dev.jorel.commandapi.CommandAPIBukkitConfig
import me.arial.zephyr.api.ZephyrPlugin
import me.arial.zephyr.api.command.ZephyrCommandManager
import me.arial.zephyr.api.event.player.PlayerMessageReceiveEvent
import me.arial.zephyr.api.module.ZephyrModule
import me.arial.zephyr.api.module.ZephyrModuleManager
import me.arial.zephyr.api.module.command.ZephyrModuleCommandManager
import me.arial.zephyr.core.command.ZephyrCommandManagerImpl
import me.arial.zephyr.core.command.impl.ZephyrCommand
import me.arial.zephyr.core.config.SettingsConfig
import me.arial.zephyr.core.module.ZephyrModuleManagerImpl
import me.arial.zephyr.core.module.command.ZephyrModuleCommandManagerImpl
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.command.Command
import java.io.File
import java.util.logging.Level

class Zephyr : ZephyrPlugin() {

    companion object {
        lateinit var instance: Zephyr
    }

    override lateinit var zephyrCommandManager: ZephyrCommandManager
    override lateinit var zephyrModuleManager: ZephyrModuleManager

    override fun enable() {
        instance = this

        if (System.getProperty("zephyr") != null) {
            logger.log(Level.SEVERE, "${ChatColor.RED}Перезагрузка Zephyr невозможна!")
            Bukkit.getPluginManager().disablePlugin(this)
            return
        }

        System.setProperty("zephyr", "enabled")

        CommandAPI.onEnable()

        zephyrCommandManager = ZephyrCommandManagerImpl()
        zephyrModuleManager = ZephyrModuleManagerImpl(
            File(
                dataFolder.path + File.separator + "modules"
            )
        )


        zephyrModuleManager.loadModules()

        zephyrCommandManager.registerCommand(ZephyrCommand())

        val settings = SettingsConfig()

        if (Bukkit.getPluginManager().getPlugin("ProtocolLib") != null && settings.playerMessageReceiveEventEnabled) {
            val protocolManager = ProtocolLibrary.getProtocolManager()

            protocolManager.addPacketListener(
                object : PacketAdapter(
                    this,
                    ListenerPriority.NORMAL,
                    PacketType.Play.Server.CHAT
                ) {
                    override fun onPacketSending(e: PacketEvent) {
                        val container = e.packet
                        val newPacket = container.deepClone()

                        val chatComponent: WrappedChatComponent = newPacket.chatComponents.read(0) ?: return

                        val playerMessageReceiveEvent = PlayerMessageReceiveEvent(e.player, chatComponent)

                        if (playerMessageReceiveEvent.isCancelled) {
                            e.isCancelled = true
                            return
                        }

                        newPacket.chatComponents.write(0, playerMessageReceiveEvent.wrappedChatComponent)

                        e.packet = newPacket
                    }
                }
            )
        }
    }

    override fun onLoad() {
        CommandAPI.onLoad(CommandAPIBukkitConfig(this).silentLogs(true).dispatcherFile(File(dataFolder, "command.json")))
    }

    override fun disable() {
        zephyrModuleManager.loadedModules().forEach {
            zephyrModuleManager.unloadModule(it.value)
        }

        zephyrCommandManager.registeredCommands.forEach {
            (it.value as Command).unregister(Bukkit.getCommandMap())
        }
    }

    override fun reload() {

    }

    override val registeredModuleCommands: MutableList<Pair<ZephyrModule, Any>> = mutableListOf()
    private val zephyrModuleCommandManagers = mutableMapOf<ZephyrModule, ZephyrModuleCommandManager>()

    override fun createModuleCommandManagerInstanceFor(module: ZephyrModule): ZephyrModuleCommandManager {
        val manager = ZephyrModuleCommandManagerImpl(module)
        zephyrModuleCommandManagers[module] = manager
        return manager
    }

    override fun getZephyrModuleCommandManager(module: ZephyrModule): ZephyrModuleCommandManager? {
        return zephyrModuleCommandManagers[module]
    }

    override fun removeZephyrModuleCommandManager(module: ZephyrModule) {
        zephyrModuleCommandManagers.remove(module)
    }
}