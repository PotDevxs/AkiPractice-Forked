/*
 * This project can be redistributed without
 * authorization of the developer
 *
 * Project @ AkiPractice
 * @author saki © 2026
 */
package aki.saki.practice.command.admin

import aki.saki.practice.Locale
import aki.saki.practice.manager.DuelBanManager
import aki.saki.practice.utils.CC
import com.jonahseguin.drink.annotation.Command
import com.jonahseguin.drink.annotation.Require
import com.jonahseguin.drink.annotation.Sender
import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer
import org.bukkit.command.CommandSender

class BanDuelCommand {

    @Command(name = "", desc = "Ban a player from duels")
    @Require("practice.command.banduel")
    fun ban(@Sender sender: CommandSender, target: OfflinePlayer, reason: String? = null) {
        if (DuelBanManager.isBanned(target.uniqueId)) {
            sender.sendMessage(CC.translate(Locale.DUEL_BAN_ALREADY_BANNED.getNormalMessage()))
            return
        }
        DuelBanManager.ban(target.uniqueId, reason)
        val msg = Locale.DUEL_BAN_PLAYER_BANNED.getNormalMessage().replace("<player>", target.name ?: target.uniqueId.toString())
        sender.sendMessage(CC.translate(msg))
    }

    @Command(name = "unban", desc = "Unban a player from duels")
    @Require("practice.command.banduel")
    fun unban(@Sender sender: CommandSender, target: OfflinePlayer) {
        if (!DuelBanManager.unban(target.uniqueId)) {
            sender.sendMessage(CC.translate(Locale.DUEL_BAN_NOT_BANNED.getNormalMessage()))
            return
        }
        val msg = Locale.DUEL_BAN_PLAYER_UNBANNED.getNormalMessage().replace("<player>", target.name ?: target.uniqueId.toString())
        sender.sendMessage(CC.translate(msg))
    }

    @Command(name = "list", desc = "List players banned from duels")
    @Require("practice.command.banduel")
    fun list(@Sender sender: CommandSender) {
        val banned = DuelBanManager.getBannedPlayers()
        val header = Locale.DUEL_BAN_LIST_HEADER.getNormalMessage().replace("<count>", banned.size.toString())
        sender.sendMessage(CC.translate(header))
        banned.forEach { uuid ->
            val name = Bukkit.getOfflinePlayer(uuid).name ?: uuid.toString()
            val reason = DuelBanManager.getReason(uuid) ?: "Sem motivo"
            val line = Locale.DUEL_BAN_LIST_ENTRY.getNormalMessage()
                .replace("<player>", name)
                .replace("<reason>", reason)
            sender.sendMessage(CC.translate(line))
        }
    }
}
