package net.runemc.plugin.script

import kotlinx.coroutines.*
import net.runemc.plugin.script.actions.load
import net.runemc.plugin.script.actions.unload
import net.runemc.plugin.script.actions.execute

import org.bukkit.command.*

class ScriptCommand(private val scriptManager: ScriptManager) : CommandExecutor, TabCompleter {

    @OptIn(DelicateCoroutinesApi::class)
    override fun onCommand(
        sender: CommandSender,
        command: Command,
        label: String,
        args: Array<out String>
    ): Boolean {
        if (args.isEmpty()) {
            sender.sendMessage("Usage: /script <load|unload|reload|execute> <scriptPath>")
            return true
        }

        val subCommand = args[0]
        val scriptName = args.getOrNull(1)

        when (subCommand) {
            "load" -> scriptName?.let { load(scriptManager, it) } ?: sender.sendMessage("Specify a script.")
            "unload" -> scriptName?.let { unload(scriptManager, it) } ?: sender.sendMessage("Specify a script.")
            "execute" -> scriptName?.let {
                GlobalScope.launch {
                    try {
                        val result = execute(scriptManager, sender, scriptName)

                        withContext(Dispatchers.IO) {
                            sender.sendMessage("Suspend function finished: $result")
                        }
                    } catch (e: Exception) {
                        withContext(Dispatchers.Main) {
                            sender.sendMessage("An error occurred: ${e.message}")
                        }
                    }
                }
            } ?: sender.sendMessage("Specify a script.")
            "reload" -> scriptName?.let {
                unload(scriptManager, it)
                load(scriptManager, it)
            } ?: sender.sendMessage("Specify a script.")
            else -> sender.sendMessage("Unknown subcommand: $subCommand")
        }
        return true
    }

    override fun onTabComplete(
            sender: CommandSender,
            command: Command,
            alias: String,
            args: Array<out String>
    ): List<String> {
        return when (args.size) {
            1 -> listOf("load", "unload", "reload", "execute").filter { it.startsWith(args[0]) }
            2 -> scriptManager.getAllScriptNames()
            else -> emptyList()
        }
    }
}
