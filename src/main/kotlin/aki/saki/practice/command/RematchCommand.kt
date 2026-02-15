/*
 * Project @ AkiPractice
 * @author saki © 2026
 */
package aki.saki.practice.command

import aki.saki.practice.Locale
import aki.saki.practice.manager.DuelBanManager
import aki.saki.practice.manager.MatchManager
import aki.saki.practice.profile.ProfileState
import aki.saki.practice.utils.CC
import com.jonahseguin.drink.annotation.Command
import com.jonahseguin.drink.annotation.Sender
import aki.saki.practice.PracticePlugin
import org.bukkit.Bukkit
import org.bukkit.entity.Player

class RematchCommand {

    @Command(name = "", desc = "Request a rematch with your last opponent")
    fun rematch(@Sender player: Player) {
        val profile = PracticePlugin.instance.profileManager.findById(player.uniqueId) ?: return
        if (profile.state != ProfileState.LOBBY) {
            player.sendMessage(CC.translate(Locale.CANT_DO_THIS.getMessage()))
            return
        }
        val last = profile.lastOpponent ?: run {
            player.sendMessage(CC.translate("&cVocê não tem um oponente recente para revanche."))
            return
        }
        val target = Bukkit.getPlayer(last) ?: run {
            player.sendMessage(CC.translate("&cO jogador não está online."))
            return
        }
        if (target.uniqueId == player.uniqueId) {
            player.sendMessage(CC.translate(Locale.CANT_DUEL_YOURSELF.getMessage()))
            return
        }
        if (DuelBanManager.isBanned(player.uniqueId)) {
            player.sendMessage(Locale.DUEL_BAN_BANNED_MSG.getMessage())
            return
        }
        if (DuelBanManager.isBanned(target.uniqueId)) {
            player.sendMessage(Locale.DUEL_BAN_BANNED_OTHER.getMessage())
            return
        }
        val targetProfile = PracticePlugin.instance.profileManager.findById(target.uniqueId)!!
        if (targetProfile.state != ProfileState.LOBBY) {
            player.sendMessage(CC.translate(Locale.BUSY_PLAYER.getMessage()))
            return
        }
        if (targetProfile.duelRequests.any { it.uuid == player.uniqueId && !it.isExpired() }) {
            player.sendMessage(CC.translate(Locale.ONGOING_DUEL.getMessage().replace("<target>", target.name)))
            return
        }
        if (!targetProfile.settings.duels && !player.hasPermission("lpractice.bypass.duels")) {
            player.sendMessage(CC.translate(Locale.DISABLED_DUELS.getMessage()))
            return
        }
        val duelProcedure = aki.saki.practice.duel.procedure.DuelProcedure(player.uniqueId, target.uniqueId)
        aki.saki.practice.duel.procedure.DuelProcedure.duelProcedures.add(duelProcedure)
        aki.saki.practice.ui.duels.DuelSelectKitMenu().openMenu(player)
    }
}
