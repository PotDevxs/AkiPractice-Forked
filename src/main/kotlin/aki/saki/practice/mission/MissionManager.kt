/*
 * Project @ AkiPractice
 * @author saki © 2026
 */
package aki.saki.practice.mission

import aki.saki.practice.PracticePlugin
import aki.saki.practice.profile.Profile
import aki.saki.practice.utils.CC
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import java.util.*

object MissionManager {

    private const val DAY_MS = 24L * 60 * 60 * 1000

    fun currentDay(): Long = System.currentTimeMillis() / DAY_MS

    /**
     * Garante reset do dia e uma missão ativa. Persiste o perfil se alterar algo.
     */
    fun checkAndResetDaily(profile: Profile) {
        val today = currentDay()
        var dirty = false
        if (profile.dailyMissionResetDay < today) {
            profile.dailyMissionType = null
            profile.dailyMissionProgress = 0
            profile.dailyMissionTarget = 0
            profile.dailyMissionResetDay = today
            dirty = true
        }
        if (profile.dailyMissionType == null) {
            assignMission(profile)
            dirty = true
        }
        if (dirty) profile.save(true)
    }

    /** Linha de progresso (sem efeitos colaterais — ex.: PlaceholderAPI). */
    fun formatMissionProgress(profile: Profile): String? {
        val typeKey = profile.dailyMissionType ?: return null
        val type = DailyMissionType.entries.find { it.key == typeKey } ?: return null
        return "${type.description}: ${profile.dailyMissionProgress}/${profile.dailyMissionTarget}"
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
            profile.save(true)
        }
    }

    private fun giveReward(profile: Profile) {
        val rewardXp = PracticePlugin.instance.settingsFile.getInt("MISSION.REWARD-XP", 100).toLong()
        if (rewardXp > 0) profile.addXp(rewardXp)
        profile.save(true)
        val player = Bukkit.getPlayer(profile.uuid) ?: return
        player.sendMessage(CC.translate("&a&lMissão diária concluída! &f+$rewardXp XP."))
    }

    fun sendMissionBookMessage(player: Player) {
        val profile = PracticePlugin.instance.profileManager.findById(player.uniqueId) ?: run {
            player.sendMessage(CC.translate("&cPerfil não encontrado."))
            return
        }
        checkAndResetDaily(profile)
        val desc = formatMissionProgress(profile)
        player.sendMessage(CC.translate("&7&m--------&r &eMissão diária &7&m--------"))
        if (desc == null) {
            player.sendMessage(CC.translate("&7Nenhuma missão ativa."))
            return
        }
        val reward = PracticePlugin.instance.settingsFile.getInt("MISSION.REWARD-XP", 100)
        player.sendMessage(CC.translate("&f$desc"))
        player.sendMessage(CC.translate("&7Recompensa: &a+$reward XP"))
    }

}
