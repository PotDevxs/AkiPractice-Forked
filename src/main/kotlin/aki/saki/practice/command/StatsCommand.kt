/*
 * Project @ AkiPractice
 * @author saki © 2026
 */
package aki.saki.practice.command

import aki.saki.practice.PracticePlugin
import aki.saki.practice.utils.CC
import com.jonahseguin.drink.annotation.Command
import com.jonahseguin.drink.annotation.Sender
import org.bukkit.Bukkit
import org.bukkit.entity.Player

class StatsCommand {

    @Command(name = "", desc = "View your or another player's stats")
    fun stats(@Sender player: Player, target: Player? = null) {
        val profile = PracticePlugin.instance.profileManager.findById((target ?: player).uniqueId) ?: run {
            player.sendMessage(CC.translate("&cPerfil não encontrado."))
            return
        }
        val name = target?.name ?: player.name
        player.sendMessage(CC.translate("&7&m--------&r &eStats de $name &7&m--------"))
        player.sendMessage(CC.translate("&fGlobal: &a${profile.globalStatistic.wins}W &c${profile.globalStatistic.losses}L"))
        profile.kitStatistics.filter { it.rankedWins > 0 || it.rankedLosses > 0 || it.wins > 0 || it.losses > 0 }
            .sortedByDescending { it.elo }
            .take(10)
            .forEach { ks ->
                val kitName = PracticePlugin.instance.kitManager.getKit(ks.kit)?.displayName ?: ks.kit
                player.sendMessage(CC.translate("&7${kitName}: &fELO &e${ks.elo} &7| &a${ks.rankedWins}W &c${ks.rankedLosses}L &7| Casual: &a${ks.wins}W &c${ks.losses}L"))
            }
    }
}
