/*
 * Project @ AkiPractice
 * @author saki © 2026
 */
package aki.saki.practice.command

import aki.saki.practice.PracticePlugin
import aki.saki.practice.utils.CC
import com.jonahseguin.drink.annotation.Command
import com.jonahseguin.drink.annotation.Sender
import org.bukkit.entity.Player
import java.text.SimpleDateFormat
import java.util.*

class HistoryCommand {

    private val dateFormat = SimpleDateFormat("dd/MM HH:mm")

    @Command(name = "", desc = "View your or another player's match history")
    fun history(@Sender player: Player, target: Player? = null) {
        val profile = PracticePlugin.instance.profileManager.findById((target ?: player).uniqueId) ?: run {
            player.sendMessage(CC.translate("&cPerfil não encontrado."))
            return
        }
        val name = target?.name ?: player.name
        val list = profile.matchHistory
        player.sendMessage(CC.translate("&7&m--------&r &eHistórico de $name &7(&f${list.size}&7) &7&m--------"))
        if (list.isEmpty()) {
            player.sendMessage(CC.translate("&7Nenhuma partida recente."))
            return
        }
        list.take(15).forEach { entry ->
            val time = dateFormat.format(Date(entry.time))
            val wl = if (entry.won) "&aV" else "&cD"
            val type = if (entry.ranked) "&6R" else "&7C"
            player.sendMessage(CC.translate("&7$wl $type &f${entry.opponentName} &8(${entry.kitName}) &7$time"))
        }
    }
}
