package net.runemc.plugin

import net.runemc.plugin.script.ScriptCommand
import net.runemc.plugin.script.ScriptManager
import org.bukkit.plugin.java.JavaPlugin

class Main : JavaPlugin() {

    override fun onEnable() {
        val scriptManager = ScriptManager(this)
        scriptManager.initialize()

        getCommand("script")?.apply {
            setExecutor(ScriptCommand(scriptManager))
            tabCompleter = ScriptCommand(scriptManager)
        }
    }

}
