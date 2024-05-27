package me.arial.zephyr.core.command

import me.arial.zephyr.api.command.*
import me.arial.zephyr.api.debug.debug
import me.arial.zephyr.api.module.ZephyrModule
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.hasAnnotation

class ZephyrCommandManagerImpl : ZephyrCommandManager {

  //  private val modulesCommands = mutableListOf<Pair<ZephyrModule, Any>>()

    override val registeredCommands: MutableMap<CommandInfo, Any> = mutableMapOf()

    override fun registerCommand(obj: Any): Boolean {
        val clazz = obj::class

        if (!clazz.hasAnnotation<CommandInfo>()) {
            throw IllegalArgumentException("§cОшибка при загрузке \"${clazz.java.name}\", аннотация @CommandInfo не существует!")
        }

        val annotation = clazz.findAnnotation<CommandInfo>()!!

        val zephyrCommand = obj as ZephyrCommand

        if (registeredCommands.containsKey(annotation)) {
            val registered = registeredCommands[annotation]!!
            (registered as ZephyrCommand).registerInstance(
                registered, true
            )
            return true
        }

        zephyrCommand.registerInstance(obj)

        registeredCommands[clazz.findAnnotation<CommandInfo>()!!] = zephyrCommand

//        if (module != null) {
//            modulesCommands.add(Pair(module, obj))
//        }
        return true
    }

    override fun unregisterCommand(obj: Any) {
        val clazz = obj::class
        val zephyrCommand = obj as ZephyrCommand

        zephyrCommand.unregister()

        registeredCommands.remove(clazz.findAnnotation<CommandInfo>())
    }

//    override fun unregisterCommands(module: ZephyrModule) {
//        modulesCommands.toList().forEach {
//            if (it.first.moduleFile!!.path == module.moduleFile!!.path) {
//                (it.second as ZephyrCommand).unregister()
//            }
//        }
//    }
}