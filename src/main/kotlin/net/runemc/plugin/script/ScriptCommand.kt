package net.runemc.plugin.script

import kotlinx.coroutines.*
import net.runemc.plugin.script.actions.execute
import net.runemc.plugin.script.actions.load
import net.runemc.plugin.script.actions.unload
import net.runemc.utils.sendMessage
import org.bukkit.Bukkit
import org.bukkit.command.*
import kotlin.script.experimental.api.ResultWithDiagnostics

class ScriptCommand(private val scriptManager: ScriptManager) : CommandExecutor, TabCompleter {

    @OptIn(DelicateCoroutinesApi::class)
    override fun onCommand(
        sender: CommandSender,
        command: Command,
        label: String,
        args: Array<out String>
    ): Boolean {
        if (args.isEmpty()) {
            sendMessage(sender, "&cInvalid Arguments! Usage: /script <load|unload|reload|execute> <scriptPath>")
            return true
        }

        val subCommand = args[0]
        val scriptName = args.getOrNull(1)

        when (subCommand) {
            "load" -> scriptName?.let {
                val start = System.currentTimeMillis();
                sendMessage(sender, "&eLoading &f$scriptName...")

                load(scriptManager, it)

                sendMessage(sender, "&aLoaded &f$scriptName! &7&o(Took ${System.currentTimeMillis()-start}ms)")
            } ?: sendMessage(sender, "&cPlease specify a script!")

            "unload" -> scriptName?.let {
                val start = System.currentTimeMillis();
                sendMessage(sender, "&eUnloading &f$scriptName...")

                unload(scriptManager, it)

                sendMessage(sender, "&aUnloaded &f$scriptName! &7&o(Took ${System.currentTimeMillis()-start}ms)")
            } ?: sendMessage(sender, "&cPlease specify a script!")

            "reload" -> scriptName?.let {
                val start = System.currentTimeMillis();
                sendMessage(sender, "&eReloading &f$scriptName...")

                unload(scriptManager, it)
                load(scriptManager, it)

                sendMessage(sender, "&aReloaded &f$scriptName! &7&o(Took ${System.currentTimeMillis()-start}ms)")
            } ?: sendMessage(sender, "&cPlease specify a script!")

            "execute" -> scriptName?.let {
                val start = System.currentTimeMillis();
                GlobalScope.launch {
                    try {
                        execute(scriptManager, scriptName)

                        withContext(Dispatchers.IO) {
                            sendMessage(sender, "&aExecuted the script: &f$scriptName &7&o(Took ${System.currentTimeMillis()-start}ms)")
                        }
                    } catch (e: Exception) {
                        withContext(Dispatchers.Main) {
                            sender.sendMessage("An error occurred: ${e.message}")
                        }
                    }
                }
            } ?: sendMessage(sender, "&cPlease specify a script!")
            else -> sender.sendMessage("&cUnknown subcommand: $subCommand!")
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
            2 -> scriptManager.getAllLoadedScriptNames()
            else -> emptyList()
        }
    }
}
