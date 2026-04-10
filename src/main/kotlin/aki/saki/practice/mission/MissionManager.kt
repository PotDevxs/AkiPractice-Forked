/*
 * Project @ AkiPractice
 * @author saki © 2026
 */
package aki.saki.practice.mission

import aki.saki.practice.PracticePlugin
import aki.saki.practice.profile.Profile
import aki.saki.practice.utils.CC
import org.bukkit.Bukkit
import java.util.*

object MissionManager {

    private const val DAY_MS = 24L * 60 * 60 * 1000

    fun currentDay(): Long = System.currentTimeMillis() / DAY_MS

    fun checkAndResetDaily(profile: Profile) {
        val last = profile.dailyMissionResetDay
        val today = currentDay()
        if (last < today) {
            profile.dailyMissionType = null
            profile.dailyMissionProgress = 0
            profile.dailyMissionTarget = 0
            profile.dailyMissionResetDay = today
            assignMission(profile)
        }
    }

    fun assignMission(profile: Profile) {
        val types = DailyMissionType.entries
        val type = types[Random().nextInt(types.size)]
        val settings = PracticePlugin.instance.settingsFile
        val target = when (type) {
            DailyMissionType.WIN_RANKED -> settings.getInt("MISSION.TARGET-WIN-RANKED", 3)
            DailyMissionType.WIN_CASUAL -> settings.getInt("MISSION.TARGET-WIN-CASUAL", 3)
            DailyMissionType.PLAY_FFA -> settings.getInt("MISSION.TARGET-PLAY-FFA", 5)
        }.coerceAtLeast(1)
        profile.dailyMissionType = type.key
        profile.dailyMissionTarget = target
        profile.dailyMissionProgress = 0
    }

    fun advance(profile: Profile, type: DailyMissionType) {
        checkAndResetDaily(profile)
        val current = profile.dailyMissionType ?: return
        if (current != type.key) return
        profile.dailyMissionProgress = (profile.dailyMissionProgress + 1).coerceAtLeast(0)
        if (profile.dailyMissionProgress >= profile.dailyMissionTarget) {
            giveReward(profile)
            assignMission(profile)
        }
    }

    private fun giveReward(profile: Profile) {
        val rewardXp = PracticePlugin.instance.settingsFile.getInt("MISSION.REWARD-XP", 100).toLong()
        if (rewardXp > 0) profile.addXp(rewardXp)
        profile.save(true)
        val player = Bukkit.getPlayer(profile.uuid) ?: return
        player.sendMessage(CC.translate("&a&lMissão diária concluída! &f+$rewardXp XP."))
    }

    fun getDescription(profile: Profile): String? {
        checkAndResetDaily(profile)
        val typeKey = profile.dailyMissionType ?: return null
        val type = DailyMissionType.entries.find { it.key == typeKey } ?: return null
        return "${type.description}: ${profile.dailyMissionProgress}/${profile.dailyMissionTarget}"
    }
}
