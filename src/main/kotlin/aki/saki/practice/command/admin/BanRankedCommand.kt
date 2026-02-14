/*
 * This project can be redistributed without
 * authorization of the developer
 *
 * Project @ AkiPractice
 * @author saki © 2026
 */
package aki.saki.practice.command.admin

import aki.saki.practice.Locale
import aki.saki.practice.manager.RankedBanManager
import aki.saki.practice.PracticePlugin
import aki.saki.practice.utils.CC
import com.jonahseguin.drink.annotation.Command
import com.jonahseguin.drink.annotation.Require
import com.jonahseguin.drink.annotation.Sender
import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer
import org.bukkit.command.CommandSender

class BanRankedCommand {

    @Command(name = "", desc = "Ban a player from ranked queue")
    @Require("practice.command.banranked")
    fun ban(@Sender sender: CommandSender, target: OfflinePlayer, reason: String? = null) {
        if (RankedBanManager.isBanned(target.uniqueId)) {
            sender.sendMessage(CC.translate(Locale.RANKED_BAN_ALREADY_BANNED.getNormalMessage()))
            return
        }
        RankedBanManager.ban(target.uniqueId, reason)
        val msg = Locale.RANKED_BAN_PLAYER_BANNED.getNormalMessage().replace("<player>", target.name ?: target.uniqueId.toString())
        sender.sendMessage(CC.translate(msg))
    }

    @Command(name = "unban", desc = "Unban a player from ranked queue")
    @Require("practice.command.banranked")
    fun unban(@Sender sender: CommandSender, target: OfflinePlayer) {
        if (!RankedBanManager.unban(target.uniqueId)) {
            sender.sendMessage(CC.translate(Locale.RANKED_BAN_NOT_BANNED.getNormalMessage()))
            return
        }
        val msg = Locale.RANKED_BAN_PLAYER_UNBANNED.getNormalMessage().replace("<player>", target.name ?: target.uniqueId.toString())
        sender.sendMessage(CC.translate(msg))
    }

    @Command(name = "list", desc = "List players banned from ranked")
    @Require("practice.command.banranked")
    fun list(@Sender sender: CommandSender) {
        val banned = RankedBanManager.getBannedPlayers()
        val header = Locale.RANKED_BAN_LIST_HEADER.getNormalMessage().replace("<count>", banned.size.toString())
        sender.sendMessage(CC.translate(header))
        banned.forEach { uuid ->
            val name = Bukkit.getOfflinePlayer(uuid).name ?: uuid.toString()
            val reason = RankedBanManager.getReason(uuid) ?: "Sem motivo"
            val line = Locale.RANKED_BAN_LIST_ENTRY.getNormalMessage()
                .replace("<player>", name)
                .replace("<reason>", reason)
            sender.sendMessage(CC.translate(line))
        }
    }
}
