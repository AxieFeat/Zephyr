package me.arial.zephyr.core.command.impl

import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.arguments.TextArgument
import dev.jorel.commandapi.executors.CommandExecutor
import me.arial.zephyr.api.Extension.Companion.sendColoredMessage
import me.arial.zephyr.api.command.CommandInfo
import me.arial.zephyr.api.command.ZephyrCommand
import me.arial.zephyr.api.command.brigadier.BrigadierCompleter
import me.arial.zephyr.api.command.brigadier.WithPermission
import me.arial.zephyr.api.prefix
import me.arial.zephyr.core.Zephyr
import org.bukkit.command.CommandSender
import java.io.File


@CommandInfo("zephyr")
class ZephyrCommand : ZephyrCommand() {

    @BrigadierCompleter
    @WithPermission("zephyr.admin")
    fun onCommand(command: CommandAPICommand): CommandAPICommand {

        val modulesSubCommand = CommandAPICommand("modules")
            .executes(CommandExecutor { sender, args ->
                modulesHandler(sender)
            })

        val moduleSubCommand = CommandAPICommand("module")
            .withArguments(
                TextArgument("module")
                    .includeSuggestions { suggestionInfo, suggestionsBuilder ->
                        Zephyr.instance.zephyrModuleManager.loadedModules().forEach {
                            suggestionsBuilder.suggest(it.key)
                        }

                        suggestionsBuilder.buildFuture()
                    }

            )
            .withArguments(
                TextArgument("action")
                    .includeSuggestions { suggestionInfo, suggestionsBuilder ->
                        suggestionsBuilder
                            .suggest("info")
                            .suggest("unload")
                            .suggest("reload")
                            .buildFuture()
                    }
            )
            .executes(dev.jorel.commandapi.executors.CommandExecutor { sender, args ->
                val module = Zephyr.instance.zephyrModuleManager.getModule(args.args[0] as String)

                if (module == null) {
                    sender.sendColoredMessage("$prefix Модуль &c${args[0]} &fне найден!")
                    return@CommandExecutor
                }

                when(args.args[1]) {
                    "info" -> {
                        sender.sendColoredMessage("$prefix Информация о &a${module.name}&f:")
                        sender.sendColoredMessage(" &c> &fВерсия: &e${module.version}")
                        sender.sendColoredMessage(" &c> &fПерезагружаемый: ${if (module.reloadable) "&aДа" else "&cНет"}")
                        sender.sendColoredMessage(" &c> &fАвторы: &9${module.authors.toList()}")
                        sender.sendColoredMessage(" &c> &fОписание: &e${module.description}")
                        sender.sendColoredMessage(" &c> &fСайт: &6${module.website}")
                        sender.sendColoredMessage(" &c> &fЗависимости:")
                        sender.sendColoredMessage("  &7- &fОбязательные: &e${module.depend.toList()}")
                        sender.sendColoredMessage("  &7- &fОпциональные: &6${module.soft.toList()}")
                    }
                    "unload" -> {
                        if (Zephyr.instance.zephyrModuleManager.unloadModule(module)) {
                            sender.sendColoredMessage("$prefix Модуль &6${module.name} &fуспешно выгружен!")
                            sender.sendColoredMessage("&#FB0824Внимание! Работоспособность модуля при повторной загрузке не гарантируется.")
                            return@CommandExecutor
                        }

                        sender.sendColoredMessage("$prefix При выгрузке &6${module.name} &fпроизошла ошибка!")
                    }
                    "reload" -> {
                        if (!module.reloadable) {
                            sender.sendColoredMessage("$prefix Для модуля &6${module.name} &fперезагрузка недоступна!")
                            return@CommandExecutor
                        }

                        if (Zephyr.instance.zephyrModuleManager.reloadModule(module)) {
                            sender.sendColoredMessage("$prefix Модуль &6${module.name} &fуспешно перезагружен!")
                            return@CommandExecutor
                        }

                        sender.sendColoredMessage("$prefix При перезагрузке &6${module.name} &fпроизошла ошибка!")
                    }

                    else -> sender.sendColoredMessage("$prefix &aПомощь: &f/zephyr module <модуль> <действие>")
                }
            })

        val loadSubCommand = CommandAPICommand("load")
            .withArguments(
                TextArgument("file")
                    .includeSuggestions { suggestionInfo, suggestionsBuilder ->
                        val files = Zephyr.instance.zephyrModuleManager.moduleFolder.listFiles {
                                _: File?, name: String -> name.endsWith(".jar")
                        }?.filter {
                            Zephyr.instance.zephyrModuleManager.loadedModules().forEach { entry ->
                                if (entry.value.module.moduleFile == it) {
                                    return@filter false
                                }
                            }
                            true
                        } ?: return@includeSuggestions suggestionsBuilder.buildFuture()

                        files.forEach {
                            suggestionsBuilder.suggest(it.name) { it.path }
                        }

                        suggestionsBuilder.buildFuture()
                    }
            )
            .executes(dev.jorel.commandapi.executors.CommandExecutor { sender, args ->
                val file = File(Zephyr.instance.dataFolder, "modules/" + args[0])

                if (!file.exists() || file.isDirectory) {
                    sender.sendColoredMessage("$prefix Файл &7\"&c${args[0]}&7\" &fне найден!")
                    return@CommandExecutor
                }

                sender.sendColoredMessage("$prefix Пытаемся загрузить модуль..")

                if (Zephyr.instance.zephyrModuleManager.loadedModules().values.map { module ->
                        module.module.moduleFile.name
                    }.toList().contains(file.name)) {

                    sender.sendColoredMessage("$prefix Модуль уже загружен!")

                    return@CommandExecutor
                }

                val module = Zephyr.instance.zephyrModuleManager.loadModule(file)

                if (module == null) {
                    sender.sendColoredMessage("$prefix При загрузке &7\"&c${args[0]}&7\" &fпроизошла ошибка!")
                    return@CommandExecutor
                }

                sender.sendColoredMessage("$prefix Модуль &7[&6${module.name} &8v${module.version}&7] &fуспешно загружен!")
            })

        command
            .withSubcommands(modulesSubCommand, moduleSubCommand, loadSubCommand)
            .executes(dev.jorel.commandapi.executors.CommandExecutor { sender, args ->
                sendUsage(sender)
            })

        return command
    }

    private fun sendUsage(sender: CommandSender) {
        sender.sendColoredMessage("$prefix Помощь по &9Zephyr&f:")
        sender.sendColoredMessage(" &c/zephyr modules &f- список всех модулей")
        sender.sendColoredMessage(" &c/zephyr module <модуль> &f- управление модулем")
        sender.sendColoredMessage(" &c/zephyr load <файл> &f- загрузить модуль")
    }

    private fun modulesHandler(sender: CommandSender) {
        val modules = Zephyr.instance.zephyrModuleManager.loadedModules()

        var message = "$prefix &fМодули &7(${modules.size})&f: "

        var count = 0
        var color = true
        modules.forEach {
            if ((count + 1) >= modules.size) {
                message += if (color) "&6${it.key}" else "&#ffad0a${it.key}"
                color = !color
            } else {
                message += if (color) "&6${it.key}, " else "&#ffad0a${it.key}, "
                color = !color
            }
            count++
        }

        sender.sendColoredMessage(message)
    }
}