package net.runemc.utils

import org.bukkit.ChatColor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

fun sendMessage(player: Player, message: String) {
    player.sendMessage(ChatColor.translateAlternateColorCodes('&', message))
}
fun sendMessage(player: CommandSender, message: String) {
    player.sendMessage(ChatColor.translateAlternateColorCodes('&', message))
}