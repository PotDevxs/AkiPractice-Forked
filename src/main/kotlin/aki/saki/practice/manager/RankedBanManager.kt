/*
 * This project can be redistributed without
 * authorization of the developer
 *
 * Project @ AkiPractice
 * @author saki © 2026
 */
package aki.saki.practice.manager

import aki.saki.practice.PracticePlugin
import org.bukkit.configuration.file.YamlConfiguration
import java.io.File
import java.util.*

object RankedBanManager {

    private val banned: MutableSet<UUID> = mutableSetOf()
    private val reasons: MutableMap<UUID, String> = mutableMapOf()
    private val file: File
        get() = File(PracticePlugin.instance.dataFolder, "rankedbans.yml")

    fun load() {
        banned.clear()
        reasons.clear()
        if (!file.exists()) return
        val config = YamlConfiguration.loadConfiguration(file)
        config.getConfigurationSection("bans")?.getKeys(false)?.forEach { uuidStr ->
            try {
                val uuid = UUID.fromString(uuidStr)
                banned.add(uuid)
                config.getString("bans.$uuidStr")?.let { reasons[uuid] = it }
            } catch (_: Exception) { }
        }
    }

    fun save() {
        file.parentFile?.mkdirs()
        val config = YamlConfiguration()
        banned.forEach { uuid ->
            config.set("bans.${uuid}", reasons[uuid] ?: "")
        }
        config.save(file)
    }

    fun isBanned(uuid: UUID): Boolean = banned.contains(uuid)

    fun getReason(uuid: UUID): String? = reasons[uuid]

    fun ban(uuid: UUID, reason: String? = null) {
        banned.add(uuid)
        if (reason != null) reasons[uuid] = reason else reasons.remove(uuid)
        save()
    }

    fun unban(uuid: UUID): Boolean {
        val removed = banned.remove(uuid)
        reasons.remove(uuid)
        if (removed) save()
        return removed
    }

    fun getBannedPlayers(): List<UUID> = banned.toList()
}
