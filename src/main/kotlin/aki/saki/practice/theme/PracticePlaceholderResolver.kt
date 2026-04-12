/*
 * Project @ AkiPractice
 * @author saki © 2026
 */
package aki.saki.practice.theme

import aki.saki.practice.PracticePlugin
import aki.saki.practice.mission.MissionManager
import org.bukkit.OfflinePlayer

object PracticePlaceholderResolver {

    fun resolve(player: OfflinePlayer?, params: String): String {
        if (player == null || !player.isOnline) return ""
        val p = player.player ?: return ""
        val profile = PracticePlugin.instance.profileManager.findById(p.uniqueId) ?: return ""
        return when (params.lowercase()) {
            "theme" -> ThemeHelper.getPrimary(p)
            "primary" -> ThemeHelper.getPrimary(p)
            "secondary" -> ThemeHelper.getSecondary(p)
            "level", "lvl" -> profile.level.toString()
            "xp" -> profile.xp.toString()
            "xp_in_level", "xpinlevel" -> (profile.xp % 100L).toString()
            "xp_to_next", "xptnext" -> xpToNext(profile.xp).toString()
            "daily_mission", "mission" -> MissionManager.formatMissionProgress(profile) ?: "-"
            "daily_mission_progress", "mission_progress" -> profile.dailyMissionProgress.toString()
            "daily_mission_target", "mission_target" -> profile.dailyMissionTarget.toString()
            "daily_mission_type", "mission_type" -> profile.dailyMissionType ?: "-"
            else -> ""
        }
    }

    private fun xpToNext(totalXp: Long): Int {
        val inLevel = (totalXp % 100L).toInt()
        return (100 - inLevel).coerceIn(1, 100)
    }
}
