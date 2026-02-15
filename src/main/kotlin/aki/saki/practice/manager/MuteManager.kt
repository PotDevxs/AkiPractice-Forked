/*
 * Project @ AkiPractice
 * @author saki © 2026
 */
package aki.saki.practice.manager

import aki.saki.practice.PracticePlugin
import org.bukkit.configuration.file.YamlConfiguration
import java.io.File
import java.util.*

object MuteManager {

    private val muted = mutableSetOf<UUID>()
    private val reasons = mutableMapOf<UUID, String>()
    private val expiresAt = mutableMapOf<UUID, Long>()
    private val file: File get() = File(PracticePlugin.instance.dataFolder, "mutes.yml")

    fun load() {
        muted.clear()
        reasons.clear()
        expiresAt.clear()
        if (!file.exists()) return
        val config = YamlConfiguration.loadConfiguration(file)
        val now = System.currentTimeMillis()
        config.getConfigurationSection("mutes")?.getKeys(false)?.forEach { uuidStr ->
            try {
                val uuid = UUID.fromString(uuidStr)
                val exp = config.getLong("mutes.$uuidStr.expiresAt", 0L)
                if (exp > 0 && exp <= now) return@forEach
                muted.add(uuid)
                config.getString("mutes.$uuidStr.reason")?.let { reasons[uuid] = it }
                if (exp > 0) expiresAt[uuid] = exp
            } catch (_: Exception) { }
        }
        save()
    }

    fun save() {
        file.parentFile?.mkdirs()
        val config = YamlConfiguration()
        muted.forEach { uuid ->
            config.set("mutes.$uuid.reason", reasons[uuid] ?: "")
            expiresAt[uuid]?.takeIf { it > 0 }?.let { config.set("mutes.$uuid.expiresAt", it) }
        }
        config.save(file)
    }

    fun isMuted(uuid: UUID): Boolean {
        if (!muted.contains(uuid)) return false
        val exp = expiresAt[uuid] ?: 0L
        if (exp > 0 && System.currentTimeMillis() >= exp) {
            muted.remove(uuid)
            reasons.remove(uuid)
            expiresAt.remove(uuid)
            save()
            return false
        }
        return true
    }

    fun getReason(uuid: UUID): String? = reasons[uuid]
    fun getExpiresAt(uuid: UUID): Long? = expiresAt[uuid]?.takeIf { it > 0 }

    fun mute(uuid: UUID, reason: String? = null, durationMillis: Long = 0L) {
        muted.add(uuid)
        if (reason != null) reasons[uuid] = reason else reasons.remove(uuid)
        if (durationMillis > 0) expiresAt[uuid] = System.currentTimeMillis() + durationMillis else expiresAt.remove(uuid)
        save()
    }

    fun unmute(uuid: UUID): Boolean {
        val ok = muted.remove(uuid)
        reasons.remove(uuid)
        expiresAt.remove(uuid)
        if (ok) save()
        return ok
    }

    fun getMutedPlayers(): List<UUID> = muted.toList()
}
