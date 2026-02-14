/*
 * This project can	 be redistributed without
 * authorization of the developer
 *
 * Project @ AkiPractice
 * @author saki © 2026
 * Date: 11/02/2026
 */
package aki.saki.practice.command

import com.jonahseguin.drink.annotation.Command
import com.jonahseguin.drink.annotation.Require
import com.jonahseguin.drink.annotation.Sender
import aki.saki.practice.Locale
import aki.saki.practice.duel.procedure.DuelProcedure
import aki.saki.practice.ui.duels.DuelSelectKitMenu
import aki.saki.practice.manager.DuelBanManager
import aki.saki.practice.manager.MatchManager
import aki.saki.practice.PracticePlugin
import aki.saki.practice.profile.ProfileState
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

/*
 * This project can't be redistributed without
 * authorization of the developer
 *
 * Project @ lPractice
 * @author yek4h © 2024
 * Date: 17/06/2024
*/

class DuelCommand {

    @Command(name = "", desc = "Duel a player")
    fun duel(@Sender sender: CommandSender, target: Player) {
        val player = sender as? Player ?: return

        if (player.uniqueId == target.uniqueId) {
            player.sendMessage(Locale.CANT_DUEL_YOURSELF.getMessage())
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

        val profile = PracticePlugin.instance.profileManager.findById(target.uniqueId)!!

        if (profile.duelRequests.any { it.uuid == player.uniqueId && !it.isExpired() }) {
            player.sendMessage(Locale.ONGOING_DUEL.getMessage().replace("<target>", target.name))
            return
        }

        if (profile.state != ProfileState.LOBBY) {
            player.sendMessage(Locale.BUSY_PLAYER.getMessage())
            return
        }

        if (!profile.settings.duels && !player.hasPermission("lpractice.bypass.duels")) {
            player.sendMessage(Locale.DISABLED_DUELS.getMessage())
            return
        }

        val duelProcedure = DuelProcedure(player.uniqueId, target.uniqueId)
        DuelProcedure.duelProcedures.add(duelProcedure)

        DuelSelectKitMenu().openMenu(player)
    }

    @Command(name = "accept", desc = "Accept a duel request")
    @Require("practice.command.duel.accept")
    fun accept(@Sender sender: CommandSender, target: Player) {
        val player = sender as? Player ?: return
        if (DuelBanManager.isBanned(player.uniqueId)) {
            player.sendMessage(Locale.DUEL_BAN_BANNED_MSG.getMessage())
            return
        }
        val profile = PracticePlugin.instance.profileManager.findById(player.uniqueId)!!
        val duelRequest = profile?.getDuelRequest(target.uniqueId)

        if (duelRequest == null) {
            player.sendMessage(Locale.INVALID_DUEL.getMessage())
            return
        }

        val arena = duelRequest.arena
        profile.duelRequests.remove(duelRequest)

        MatchManager.createMatch(
            duelRequest.kit,
            arena,
            null,
            true,
            player,
            target
        )
    }
}
