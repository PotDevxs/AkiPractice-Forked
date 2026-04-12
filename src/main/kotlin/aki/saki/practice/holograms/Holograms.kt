package aki.saki.practice.holograms

import aki.saki.practice.PracticePlugin
import aki.saki.practice.nms.NmsBridge
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.scheduler.BukkitRunnable
import org.bukkit.scheduler.BukkitTask
import java.io.Serializable
import java.util.ArrayList
import java.util.concurrent.ConcurrentHashMap
import kotlin.math.pow

abstract class Holograms(val location: Location, val time: Int, val refreshHologram: Int, var name: String? = null, val distance: Double, val distanceCalcByListener: Boolean) {

    private val armorStands: MutableMap<Int, Any> = ConcurrentHashMap()
    lateinit var run: BukkitTask
    val lines: MutableList<String> = ArrayList()
    var actualTime: Int = time
    var updatable: Boolean = true
    var updated = false
    var itemTop: Boolean = true

    fun start() {
        NmsBridge.ensureLoaded()
        update()
        updateLines()

        if (updatable) {
            run = object : BukkitRunnable() {
                override fun run() {
                    try {
                        tick()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }.runTaskTimerAsynchronously(PracticePlugin.instance, 20L, 20L * refreshHologram.toLong())
        } else {
            tick()
        }
    }

    fun stop() {
        if (this::run.isInitialized) {
            run.cancel()
        }
        armorStands.forEach { (index, armorStand) ->
            val line = lines.getOrNull(index) ?: ""
            Bukkit.getOnlinePlayers().forEach { player ->
                hide(player, mapOf(armorStand to line))
            }
        }
        armorStands.clear()
    }

    abstract fun update()

    abstract fun updateLines()

    open fun tick() {
        updated = true
        actualTime--

        if (actualTime < 1) {
            actualTime = time
            update()
        }

        updateLines()
    }

    open fun updateLine(index: Int, line: String) {
        lines[index] = line
        val armorStand = armorStands[index]
        armorStand?.let {
            NmsBridge.armorStandSetCustomName(it, line)
            NmsBridge.armorStandSetCustomNameVisible(it, !line.equals("<void>", ignoreCase = true))
            Bukkit.getOnlinePlayers().forEach { player ->
                updateArmorStand(player, it)
            }
        }
    }

    open fun addLine(line: String) {
        var y = location.y - (lines.size * 0.25)
        if (line.isBlank()) {
            y -= 0.25
            lines.add("<void>")
        } else {
            val stand = NmsBridge.newEntityArmorStand(location.world!!, location.x, y, location.z)
            NmsBridge.armorStandSetCustomName(stand, line)
            NmsBridge.armorStandSetCustomNameVisible(stand, true)
            NmsBridge.armorStandSetInvisible(stand, true)
            NmsBridge.armorStandSetSmall(stand, true)
            armorStands[lines.size] = stand
            lines.add(line)
            Bukkit.getOnlinePlayers().forEach { player ->
                show(player, mapOf(stand to line))
            }
        }
    }

    open fun removeExtraLines(newSize: Int) {
        for (i in newSize until lines.size) {
            val armorStand = armorStands[i]
            armorStand?.let {
                armorStands.remove(i)
                Bukkit.getOnlinePlayers().forEach { player ->
                    hide(player, mapOf(it to lines.getOrNull(i) ?: ""))
                }
            }
        }
        lines.subList(newSize, lines.size).clear()
    }

    open fun show(player: Player, standsToShow: Map<Any, String>) {
        standsToShow.forEach { (armorStand, line) ->
            NmsBridge.armorStandSetCustomName(armorStand, line)
            val spawn = NmsBridge.newPacketSpawnEntityLiving(armorStand)
            val meta = NmsBridge.newPacketEntityMetadata(
                NmsBridge.getEntityId(armorStand),
                NmsBridge.getDataWatcher(armorStand),
                true
            )
            NmsBridge.sendPacket(player, spawn)
            NmsBridge.sendPacket(player, meta)
        }
    }

    open fun updateArmorStand(player: Player, armorStand: Any) {
        val meta = NmsBridge.newPacketEntityMetadata(
            NmsBridge.getEntityId(armorStand),
            NmsBridge.getDataWatcher(armorStand),
            true
        )
        NmsBridge.sendPacket(player, meta)
    }

    open fun show(player: Player) {
        armorStands.values.forEach { armorStand ->
            val spawn = NmsBridge.newPacketSpawnEntityLiving(armorStand)
            val meta = NmsBridge.newPacketEntityMetadata(
                NmsBridge.getEntityId(armorStand),
                NmsBridge.getDataWatcher(armorStand),
                true
            )
            NmsBridge.sendPacket(player, spawn)
            NmsBridge.sendPacket(player, meta)
        }
    }

    open fun hide(player: Player) {
        armorStands.values.forEach { armorStand ->
            NmsBridge.sendPacket(player, NmsBridge.newPacketEntityDestroy(NmsBridge.getEntityId(armorStand)))
        }
    }

    protected open fun hide(player: Player, armorStands: Map<Any, String>) {
        armorStands.keys.forEach { armorStand ->
            NmsBridge.sendPacket(player, NmsBridge.newPacketEntityDestroy(NmsBridge.getEntityId(armorStand)))
        }
    }

    fun isPlayerNearLocation(player: Player, location: Location): Boolean {
        val playerLocation = player.location
        val distanceSquared = location.distanceSquared(playerLocation)
        val rangeSquared = distance.pow(2)

        return distanceSquared <= rangeSquared
    }
}

private fun Holograms.mapOf(pair: Serializable): Map<Any, String> {
    return TODO("Provide the return value")
}
