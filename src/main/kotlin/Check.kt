package top.e404.dancetree

import org.bukkit.entity.Player
import kotlin.math.abs

data class Check(
    val p: Player,
    var times: Int = 0,
    val x: Int = p.location.blockX,
    val y: Int = p.location.blockY,
    val z: Int = p.location.blockZ,
) {
    fun match(p: Player) =
        p.location.let {
            when {
                abs(it.blockX - x) > CheckManager.distance -> false
                abs(it.blockY - y) > CheckManager.distance -> false
                abs(it.blockZ - z) > CheckManager.distance -> false
                else -> true
            }
        }
}