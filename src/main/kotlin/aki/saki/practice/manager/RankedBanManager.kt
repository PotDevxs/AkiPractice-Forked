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

    private val banned = mutableSetOf<UUID>()
    private val reasons = mutableMapOf<UUID, String>()
    /** Expiração em millis (0 = permanente). */
    private val expiresAt = mutableMapOf<UUID, Long>()
    private val file: File get() = File(PracticePlugin.instance.dataFolder, "rankedbans.yml")

    fun load() {
        banned.clear()
        reasons.clear()
        expiresAt.clear()
        if (!file.exists()) return
        val config = YamlConfiguration.loadConfiguration(file)
        val now = System.currentTimeMillis()
        config.getConfigurationSection("bans")?.getKeys(false)?.forEach { uuidStr ->
            try {
                val uuid = UUID.fromString(uuidStr)
                val exp = config.getLong("bans.$uuidStr.expiresAt", 0L)
                if (exp > 0 && exp <= now) return@forEach
                banned.add(uuid)
                val reason = config.getString("bans.$uuidStr.reason") ?: config.getString("bans.$uuidStr")
                reason?.let { reasons[uuid] = it }
                if (exp > 0) expiresAt[uuid] = exp
            } catch (_: Exception) { }
        }
        save()
    }

    fun save() {
        file.parentFile?.mkdirs()
        val config = YamlConfiguration()
        banned.forEach { uuid ->
            config.set("bans.$uuid.reason", reasons[uuid] ?: "")
            expiresAt[uuid]?.takeIf { it > 0 }?.let { config.set("bans.$uuid.expiresAt", it) }
        }
        config.save(file)
    }

    fun isBanned(uuid: UUID): Boolean {
        if (!banned.contains(uuid)) return false
        val exp = expiresAt[uuid] ?: 0L
        if (exp > 0 && System.currentTimeMillis() >= exp) {
            banned.remove(uuid)
            reasons.remove(uuid)
            expiresAt.remove(uuid)
            save()
            return false
        }
        return true
    }

    fun getReason(uuid: UUID): String? = reasons[uuid]
    fun getExpiresAt(uuid: UUID): Long? = expiresAt[uuid]?.takeIf { it > 0 }

    fun ban(uuid: UUID, reason: String? = null, durationMillis: Long = 0L) {
        banned.add(uuid)
        if (reason != null) reasons[uuid] = reason else reasons.remove(uuid)
        if (durationMillis > 0) expiresAt[uuid] = System.currentTimeMillis() + durationMillis else expiresAt.remove(uuid)
        save()
    }

    fun unban(uuid: UUID): Boolean {
        val removed = banned.remove(uuid)
        reasons.remove(uuid)
        expiresAt.remove(uuid)
        if (removed) save()
        return removed
    }

    fun getBannedPlayers(): List<UUID> = banned.toList()
}
