/*
 * Project @ AkiPractice
 * @author saki © 2026
 */
package aki.saki.practice.command

import aki.saki.practice.PracticePlugin
import aki.saki.practice.mission.MissionManager
import aki.saki.practice.utils.CC
import com.jonahseguin.drink.annotation.Command
import com.jonahseguin.drink.annotation.Sender
import org.bukkit.entity.Player

class MissionCommand {

    @Command(name = "", desc = "View your daily mission")
    fun mission(@Sender player: Player) {
        val profile = PracticePlugin.instance.profileManager.findById(player.uniqueId) ?: run {
            player.sendMessage(CC.translate("&cPerfil não encontrado."))
            return
        }
        MissionManager.checkAndResetDaily(profile)
        val desc = MissionManager.getDescription(profile)
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
