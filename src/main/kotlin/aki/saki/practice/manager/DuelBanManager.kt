/*
 * This project can be redistributed without
 * authorization of the developer
 * Project @ AkiPractice
 */
package aki.saki.practice.manager

import aki.saki.practice.PracticePlugin
import org.bukkit.configuration.file.YamlConfiguration
import java.io.File
import java.util.*

object DuelBanManager {

    private val banned = mutableSetOf<UUID>()
    private val reasons = mutableMapOf<UUID, String>()
    private val file: File get() = File(PracticePlugin.instance.dataFolder, "duelbans.yml")

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
        banned.forEach { config.set("bans.$it", reasons[it] ?: "") }
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
        val ok = banned.remove(uuid)
        reasons.remove(uuid)
        if (ok) save()
        return ok
    }

    fun getBannedPlayers(): List<UUID> = banned.toList()
}
