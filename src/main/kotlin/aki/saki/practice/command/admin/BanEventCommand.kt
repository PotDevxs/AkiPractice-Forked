/*
 * This project can be redistributed without
 * authorization of the developer
 *
 * Project @ AkiPractice
 * @author saki © 2026
 */
package aki.saki.practice.command.admin

import aki.saki.practice.Locale
import aki.saki.practice.manager.EventBanManager
import aki.saki.practice.utils.CC
import com.jonahseguin.drink.annotation.Command
import com.jonahseguin.drink.annotation.Require
import com.jonahseguin.drink.annotation.Sender
import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer
import org.bukkit.command.CommandSender

class BanEventCommand {

    @Command(name = "", desc = "Ban a player from events")
    @Require("practice.command.banevent")
    fun ban(@Sender sender: CommandSender, target: OfflinePlayer, reason: String? = null) {
        if (EventBanManager.isBanned(target.uniqueId)) {
            sender.sendMessage(CC.translate(Locale.EVENT_BAN_ALREADY_BANNED.getNormalMessage()))
            return
        }
        EventBanManager.ban(target.uniqueId, reason)
        val msg = Locale.EVENT_BAN_PLAYER_BANNED.getNormalMessage().replace("<player>", target.name ?: target.uniqueId.toString())
        sender.sendMessage(CC.translate(msg))
    }

    @Command(name = "unban", desc = "Unban a player from events")
    @Require("practice.command.banevent")
    fun unban(@Sender sender: CommandSender, target: OfflinePlayer) {
        if (!EventBanManager.unban(target.uniqueId)) {
            sender.sendMessage(CC.translate(Locale.EVENT_BAN_NOT_BANNED.getNormalMessage()))
            return
        }
        val msg = Locale.EVENT_BAN_PLAYER_UNBANNED.getNormalMessage().replace("<player>", target.name ?: target.uniqueId.toString())
        sender.sendMessage(CC.translate(msg))
    }

    @Command(name = "list", desc = "List players banned from events")
    @Require("practice.command.banevent")
    fun list(@Sender sender: CommandSender) {
        val banned = EventBanManager.getBannedPlayers()
        val header = Locale.EVENT_BAN_LIST_HEADER.getNormalMessage().replace("<count>", banned.size.toString())
        sender.sendMessage(CC.translate(header))
        banned.forEach { uuid ->
            val name = Bukkit.getOfflinePlayer(uuid).name ?: uuid.toString()
            val reason = EventBanManager.getReason(uuid) ?: "Sem motivo"
            val line = Locale.EVENT_BAN_LIST_ENTRY.getNormalMessage()
                .replace("<player>", name)
                .replace("<reason>", reason)
            sender.sendMessage(CC.translate(line))
        }
    }
}
