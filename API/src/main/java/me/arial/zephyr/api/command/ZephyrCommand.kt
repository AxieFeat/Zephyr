package me.arial.zephyr.api.command

import com.mojang.brigadier.tree.LiteralCommandNode
import dev.jorel.commandapi.CommandAPICommand
import me.arial.zephyr.api.*
import me.arial.zephyr.api.command.brigadier.BrigadierCompleter
import me.arial.zephyr.api.command.brigadier.WithPermission
import me.arial.zephyr.api.command.brigadier.WithoutPermission
import me.arial.zephyr.api.command.commodore.CommodoreCompleter
import me.arial.zephyr.api.command.commodore.CommodorePermission
import me.arial.zephyr.api.text.LangComponent
import me.lucko.commodore.CommodoreProvider
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import kotlin.reflect.KFunction
import kotlin.reflect.full.declaredFunctions
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.hasAnnotation
import kotlin.reflect.jvm.isAccessible

abstract class ZephyrCommand : Command("") {

    private var enabled = false
    private var clazz: Class<out Any>? = null
    private var instance: Any? = null
    private var commands = mutableMapOf<CommandExecutor, KFunction<*>>()
    private var completers = mutableMapOf<CommandCompleter, KFunction<*>>()

    override fun execute(sender: CommandSender, alias: String, args: Array<out String>?): Boolean {
        if (!enabled) {
            sender.sendColoredMessage("$prefix Команда отключена!")
            return true
        }

        commands.forEach {
            if (it.key.command.contains(alias) || it.key.command[0] == "") {
                it.value.isAccessible = true

                val result = it.value.call(
                    instance!!,
                    sender,
                    alias,
                    this as Command,
                    args
                )

                when (result) {
                    is String? -> {
                        if (result != null) {
                            if (it.key.color) sender.sendColoredMessage(result) else sender.sendMessage(result)
                        }
                    }

                    is LangComponent? -> {
                        if (result != null) {
                            if (sender is Player) {
                                sender.sendLangComponent(result)
                            } else
                                result.components!!.forEach { component ->
                                    sender.sendMessage(component)
                                }
                        }
                    }
                }

//                Packet(
//                    type = PacketType.COMMAND_EXECUTOR,
//                    content = arrayOf(
//                        "sender ${sender.name}",
//                        "alias $alias",
//                        "args ${(args ?: arrayOf(" ")).toList().toString().cut(150)}"
//                    )
//                ).send()

                return true
            }
        }

//        Packet(
//            type = PacketType.COMMAND_EXECUTOR,
//            content = arrayOf(
//                "sender ${sender.name}",
//                "alias $alias",
//                "args ${(args ?: arrayOf(" ")).toList().toString().cut(150)}"
//            )
//        ).send()

        return true
    }

    override fun tabComplete(sender: CommandSender, alias: String, args: Array<out String>?): MutableList<String> {
        if (!enabled) return mutableListOf()

        completers.forEach {
            if (it.key.command.contains(alias) || it.key.command[0] == "") {
                it.value.isAccessible = true

                val result = it.value.call(
                    instance!!,
                    sender,
                    alias,
                    this as Command,
                    if (it.key.advancedArgs) args.parseArgs() else args
                ) as MutableList<String>

//                Packet(
//                    type = PacketType.COMMAND_COMPLETER,
//                    content = arrayOf(
//                        "sender ${sender.name}",
//                        "alias $alias",
//                        "result $result"
//                    )
//                ).send()

                return result
            }
        }

//        Packet(
//            type = PacketType.COMMAND_COMPLETER,
//            content = arrayOf(
//                "sender ${sender.name}",
//                "alias $alias",
//                "result"
//            )
//        ).send()

        return mutableListOf()
    }

    fun registerInstance(obj: Any, exist: Boolean = false) {
        if (exist) {
            enabled = true
            return
        }

        this.clazz = obj::class.java
        instance = obj

        val annotation = clazz!!.kotlin.findAnnotation<CommandInfo>()!!

        val brigadierCompleter = clazz!!.kotlin.declaredFunctions.filter { it.hasAnnotation<BrigadierCompleter>() }.firstOrNull()
        if (brigadierCompleter != null) {
            val command = CommandAPICommand(annotation.name).withAliases(*annotation.aliases).withFullDescription(annotation.description)

            val withPermission = brigadierCompleter.findAnnotation<WithPermission>()
            if (withPermission != null) {
                command.withPermission(withPermission.permission)
            }

            val withoutPermission = brigadierCompleter.findAnnotation<WithoutPermission>()
            if (withoutPermission != null) {
                command.withoutPermission(withoutPermission.permission)
            }

            val commandAPICommand = brigadierCompleter.call(instance, command) as CommandAPICommand

            commandAPICommand.register()
            return
        }

        setName(annotation.name)
        setLabel(annotation.name)
        setAliases(annotation.aliases.toMutableList())
        setDescription(annotation.description)

        val commandExecutor = clazz!!.kotlin.declaredFunctions.filter { it.hasAnnotation<CommandExecutor>() }

        commandExecutor.forEach {
            commands[it.findAnnotation<CommandExecutor>()!!] = it
        }

        val commandCompleter = clazz!!.kotlin.declaredFunctions.filter { it.hasAnnotation<CommandCompleter>() }

        commandCompleter.forEach {
            completers[it.findAnnotation<CommandCompleter>()!!] = it
        }

        val commodoreCompleter = clazz!!.kotlin.declaredFunctions.filter { it.hasAnnotation<CommodoreCompleter>() }

        commodoreCompleter.forEach {
            val call = it.call(instance) as LiteralCommandNode<*>?

            if (call != null) {
                CommodoreProvider.getCommodore(ZephyrPlugin.instance).register(
                    this as Command,
                    call,
                ) { player ->
                    if (it.hasAnnotation<CommodorePermission>()) {
                        player.hasPermission(it.findAnnotation<CommodorePermission>()!!.permission)
                    } else {
                        true
                    }
                }
            }
        }

        Bukkit.getCommandMap().register(
            annotation.name,
            instance as ZephyrCommand
        )

        enabled = true
    }

    fun unregister() {
        enabled = false
    }

    open fun onCommand(sender: CommandSender, name: String, command: Command, args: Array<out String>?): String? = null
    open fun onCommandComplete(sender: CommandSender, name: String, command: Command, args: Array<out String>?): List<String> = mutableListOf()
    open fun commodoreCompleter(): LiteralCommandNode<*>? = null
}