package top.e404.dancetree

import me.mrCookieSlime.Slimefun.api.BlockStorage
import org.bukkit.GameMode
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.TreeType
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerToggleSneakEvent
import kotlin.random.Random

object CheckManager : Listener {
    private val map = hashMapOf<Player, Check>()

    // cfg
    private var times = 100
    private var chance = 50.0
    private var xRange = -2..2
    private var yRange = -1..1
    private var zRange = -2..2
    var distance = 1
    private var success = "§a树长出来啦"
    private var fail = "§c树长不出来"

    fun String.color() = replace("&", "§")

    private fun ConfigurationSection.getRange(path: String) =
        getString(path)?.let {
            val split = it.split("..")
            split[0].toInt()..split[1].toInt()
        }

    fun onReload() {
        DanceTree.instance.config.apply {
            times = getInt("times")
            chance = getDouble("chance")
            xRange = getRange("x")!!
            yRange = getRange("y")!!
            zRange = getRange("z")!!
            distance = getInt("distance")
            success = getString("success")?.color() ?: ""
            fail = getString("fail")?.color() ?: ""
        }
    }

    private val allow = mapOf<Material, (Location) -> Boolean>(
        // 橡木
        Material.OAK_SAPLING to { it.world!!.generateTree(it, TreeType.TREE) },
        // 白桦
        Material.BIRCH_SAPLING to { it.world!!.generateTree(it, TreeType.BIRCH) },
        // 云杉
        Material.SPRUCE_SAPLING to { it.world!!.generateTree(it, TreeType.REDWOOD) },
        // 丛林
        Material.JUNGLE_SAPLING to { it.world!!.generateTree(it, TreeType.JUNGLE) },
        /* 一个树苗也能催生
        // 深色橡木
        Material.DARK_OAK_SAPLING to { it.world!!.generateTree(it, TreeType.DARK_OAK) },*/
        // 金合欢
        Material.ACACIA_SAPLING to { it.world!!.generateTree(it, TreeType.ACACIA) },
        // 绯红菌
        Material.CRIMSON_FUNGUS to { it.world!!.generateTree(it, TreeType.CRIMSON_FUNGUS) },
        // 诡异菌
        Material.WARPED_FUNGUS to { it.world!!.generateTree(it, TreeType.WARPED_FUNGUS) },
    )

    @EventHandler
    fun PlayerToggleSneakEvent.onSneak() {
        if (player.gameMode != GameMode.SURVIVAL) return
        val check = map.getOrPut(player) { Check(player) }
        if (!check.match(player)) {
            map.remove(player)
            return
        }
        check.times++
        if (check.times >= times) {
            check.times = 0
            // 未触发
            if (Random.nextDouble(0.0, 100.0) > chance) return
            // 触发
            player.location.apply {
                a@ for (x in xRange) for (y in yRange) for (z in zRange) {
                    val b = player.world.getBlockAt(blockX + x, blockY + y, blockZ + z)
                    if (DanceTree.withSf && BlockStorage.hasBlockInfo(b)) continue
                    val data = b.blockData.clone()
                    val function = allow[b.type] ?: continue
                    b.type = Material.AIR
                    if (function.invoke(b.location)) {
                        if (success != "") player.sendMessage(success)
                    } else {
                        b.blockData = data
                        if (fail != "") player.sendMessage(fail)
                    }
                    break@a
                }
            }
        }
    }

}