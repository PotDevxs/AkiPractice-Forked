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
import aki.saki.practice.utils.CC
import com.jonahseguin.drink.annotation.Command
import com.jonahseguin.drink.annotation.Require
import com.jonahseguin.drink.annotation.Sender
import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer
import org.bukkit.command.CommandSender

class BanRankedCommand {

    @Command(name = "", desc = "Ban a player from ranked. Use: /banranked <player> [reason] or [reason] 7d for temp ban")
    @Require("practice.command.banranked")
    fun ban(@Sender sender: CommandSender, target: OfflinePlayer, reason: String? = null) {
        if (RankedBanManager.isBanned(target.uniqueId)) {
            sender.sendMessage(CC.translate(Locale.RANKED_BAN_ALREADY_BANNED.getNormalMessage()))
            return
        }
        var parsedReason: String? = null
        var durationMillis = 0L
        if (!reason.isNullOrBlank()) {
            val parts = reason.trim().split(Regex("\\s+"))
            val last = parts.lastOrNull()?.lowercase() ?: ""
            if (last.matches(Regex("\\d+[dhms]"))) {
                durationMillis = parseDuration(last)
                parsedReason = parts.dropLast(1).joinToString(" ").ifBlank { null }
            } else {
                parsedReason = reason.trim()
            }
        }
        RankedBanManager.ban(target.uniqueId, parsedReason, durationMillis)
        val msg = Locale.RANKED_BAN_PLAYER_BANNED.getNormalMessage().replace("<player>", target.name ?: target.uniqueId.toString())
        sender.sendMessage(CC.translate(msg))
    }

    private fun parseDuration(input: String): Long {
        if (input.isBlank()) return 0L
        val s = input.trim().lowercase()
        val multiplier = when {
            s.endsWith("d") -> 24 * 60 * 60 * 1000L
            s.endsWith("h") -> 60 * 60 * 1000L
            s.endsWith("m") -> 60 * 1000L
            s.endsWith("s") -> 1000L
            else -> return 0L
        }
        val num = s.dropLast(1).toLongOrNull() ?: return 0L
        return if (num <= 0) 0L else num * multiplier
    }

    private fun formatTimeRemaining(expiresAt: Long): String {
        var rem = (expiresAt - System.currentTimeMillis()).coerceAtLeast(0)
        if (rem == 0L) return "agora"
        val days = rem / (24 * 60 * 60 * 1000)
        rem %= (24 * 60 * 60 * 1000)
        val hours = rem / (60 * 60 * 1000)
        rem %= (60 * 60 * 1000)
        val minutes = rem / (60 * 1000)
        return buildString {
            if (days > 0) append("${days}d ")
            if (hours > 0) append("${hours}h ")
            if (minutes > 0 || isEmpty()) append("${minutes}m")
        }.trim()
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
            val exp = RankedBanManager.getExpiresAt(uuid)
            val line = if (exp != null && exp > 0) {
                Locale.RANKED_BAN_LIST_ENTRY_EXPIRES.getNormalMessage()
                    .replace("<player>", name)
                    .replace("<reason>", reason)
                    .replace("<time>", formatTimeRemaining(exp))
            } else {
                Locale.RANKED_BAN_LIST_ENTRY.getNormalMessage()
                    .replace("<player>", name)
                    .replace("<reason>", reason)
            }
            sender.sendMessage(CC.translate(line))
        }
    }
}
