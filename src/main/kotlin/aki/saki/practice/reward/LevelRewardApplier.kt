/*
 * Project @ AkiPractice
 * @author saki © 2026
 */
package aki.saki.practice.reward

import aki.saki.practice.PracticePlugin
import aki.saki.practice.profile.Profile
import aki.saki.practice.utils.CC
import aki.saki.practice.utils.ConfigFile
import aki.saki.practice.utils.ItemBuilder
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

object LevelRewardApplier {

    fun afterXpChange(profile: Profile, xpBefore: Long, xpAfter: Long) {
        val settings = PracticePlugin.instance.settingsFile
        if (!settings.config.getBoolean("LEVEL-REWARDS.ENABLED", false)) return
        val oldLevel = (xpBefore / 100L).toInt().coerceAtLeast(0)
        val newLevel = (xpAfter / 100L).toInt().coerceAtLeast(0)
        if (newLevel <= oldLevel) return
        val levels = settings.config.getConfigurationSection("LEVEL-REWARDS.LEVELS") ?: return
        val online = Bukkit.getPlayer(profile.uuid)
        val displayName = profile.name ?: online?.name ?: ""
        for (lv in oldLevel + 1..newLevel) {
            val sec = levels.getConfigurationSection(lv.toString()) ?: continue
            grant(profile, online, displayName, lv, sec, settings)
        }
    }

    private fun grant(
        profile: Profile,
        online: Player?,
        displayName: String,
        level: Int,
        sec: ConfigurationSection,
        settings: ConfigFile
    ) {
        val defaultUp = settings.config.getString("LEVEL-REWARDS.DEFAULT-LEVEL-UP-MESSAGE")
            ?: "&eSubiste para o nível &f%level%&e!"
        val customUp = sec.getString("LEVEL-UP-MESSAGE")
        val msg = customUp ?: defaultUp
        if (msg.isNotBlank() && online != null) {
            online.sendMessage(CC.translate(applyTokens(msg, displayName, profile, level)))
        }
        sec.getStringList("MESSAGES").filter { it.isNotBlank() }.forEach { line ->
            if (online != null) online.sendMessage(CC.translate(applyTokens(line, displayName, profile, level)))
        }
        sec.getStringList("COMMANDS").filter { it.isNotBlank() }.forEach { raw ->
            val cmd = applyTokens(raw, displayName, profile, level)
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd)
        }
        if (online != null) {
            sec.getStringList("ITEMS").filter { it.isNotBlank() }.forEach { line ->
                parseItem(line)?.let { stack ->
                    val left = online.inventory.addItem(stack)
                    left.values.forEach { leftover -> online.world.dropItemNaturally(online.location, leftover) }
                }
            }
        }
        if (settings.config.getBoolean("LEVEL-REWARDS.BROADCAST", false)) {
            val fmt = settings.config.getString("LEVEL-REWARDS.BROADCAST-MESSAGE")
                ?: "&e%player% &7subiu para o nível &f%level%&7!"
            Bukkit.broadcastMessage(CC.translate(applyTokens(fmt, displayName, profile, level)))
        }
    }

    private fun applyTokens(s: String, name: String, profile: Profile, level: Int): String =
        s.replace("%player%", name)
            .replace("%uuid%", profile.uuid.toString())
            .replace("%level%", level.toString())

    private fun parseItem(line: String): ItemStack? {
        val parts = line.split(":").map { it.trim() }.filter { it.isNotEmpty() }
        if (parts.isEmpty()) return null
        val mat = try {
            Material.valueOf(parts[0].uppercase())
        } catch (_: Exception) {
            return null
        }
        val amount = parts.getOrNull(1)?.toIntOrNull()?.coerceIn(1, 64) ?: 1
        return ItemBuilder(mat).amount(amount).build()
    }
}
