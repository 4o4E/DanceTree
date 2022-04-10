package top.e404.dancetree

import org.bstats.bukkit.Metrics
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.plugin.java.JavaPlugin
import top.e404.dancetree.CheckManager.color

class DanceTree : JavaPlugin() {
    companion object {
        @JvmStatic
        lateinit var instance: DanceTree

        @JvmStatic
        var withSf = false
    }

    override fun onEnable() {
        instance = this
        withSf = Bukkit.getPluginManager().getPlugin("Slimefun").let { it != null && it.isEnabled }
        Metrics(this, 14897)
        saveDefaultConfig()
        reloadConfig()
        CheckManager.onReload()
        Bukkit.getPluginManager().registerEvents(CheckManager, this)
    }

    override fun onCommand(
        sender: CommandSender,
        command: Command,
        label: String,
        args: Array<out String>,
    ): Boolean {
        saveDefaultConfig()
        reloadConfig()
        CheckManager.onReload()
        sender.sendMessage("&a重载完成".color())
        return true
    }
}