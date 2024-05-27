package me.arial.zephyr.core.module.command

import me.arial.zephyr.api.command.CommandInfo
import me.arial.zephyr.api.command.ZephyrCommand
import me.arial.zephyr.api.module.ZephyrModule
import me.arial.zephyr.api.module.command.ZephyrModuleCommandManager
import me.arial.zephyr.core.Zephyr

class ZephyrModuleCommandManagerImpl(private val zephyrModule: ZephyrModule) : ZephyrModuleCommandManager {

    override fun unregisterCommands() {
        Zephyr.instance.registeredModuleCommands.toList().forEach {
            if (it.first.moduleFile.path == zephyrModule.moduleFile.path) {
                (it.second as ZephyrCommand).unregister()
            }
        }
    }

    override val registeredCommands: MutableMap<CommandInfo, Any>
        get() = Zephyr.instance.zephyrCommandManager.registeredCommands

    override fun registerCommand(obj: Any): Boolean {
        if (Zephyr.instance.zephyrCommandManager.registerCommand(obj)) {
            Zephyr.instance.registeredModuleCommands.add(Pair(zephyrModule, obj))
            return true
        }
        return false
    }

    override fun unregisterCommand(obj: Any) {
        Zephyr.instance.zephyrCommandManager.unregisterCommand(obj)
        Zephyr.instance.registeredModuleCommands.remove(Pair(zephyrModule, obj))
    }

}