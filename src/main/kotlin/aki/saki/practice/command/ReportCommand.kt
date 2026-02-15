/*
 * Project @ AkiPractice
 * @author saki © 2026
 */
package aki.saki.practice.command

import aki.saki.practice.manager.ReportManager
import aki.saki.practice.utils.CC
import com.jonahseguin.drink.annotation.Command
import com.jonahseguin.drink.annotation.Require
import com.jonahseguin.drink.annotation.Sender
import org.bukkit.Bukkit
import org.bukkit.entity.Player

class ReportCommand {

    @Command(name = "", desc = "Report a player")
    @Require("practice.command.report")
    fun report(@Sender player: Player, target: Player, reason: String) {
        if (target.uniqueId == player.uniqueId) {
            player.sendMessage(CC.translate("&cVocê não pode reportar a si mesmo."))
            return
        }
        if (reason.isBlank()) {
            player.sendMessage(CC.translate("&cUse: /report <jogador> <motivo>"))
            return
        }
        ReportManager.add(player.uniqueId, target.uniqueId, player.name ?: "", target.name ?: "", reason.trim())
        player.sendMessage(CC.translate("&aReport enviado. A equipe analisará em breve."))
        Bukkit.getOnlinePlayers().filter { it.hasPermission("practice.command.reports") }.forEach {
            it.sendMessage(CC.translate("&e[Report] &c${player.name} &7reportou &c${target.name} &7: &f$reason"))
        }
    }
}
