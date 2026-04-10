/*
 * Project @ AkiPractice
 * @author saki © 2026
 */
package aki.saki.practice.command.admin

import aki.saki.practice.manager.MuteManager
import aki.saki.practice.utils.CC
import com.jonahseguin.drink.annotation.Command
import com.jonahseguin.drink.annotation.Require
import com.jonahseguin.drink.annotation.Sender
import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer
import org.bukkit.command.CommandSender

class MuteCommand {

    @Command(name = "", desc = "Mute a player. Usage: /mute <player> [reason] or [reason] 7d")
    @Require("practice.command.mute")
    fun mute(@Sender sender: CommandSender, target: OfflinePlayer, reason: String? = null) {
        if (MuteManager.isMuted(target.uniqueId)) {
            sender.sendMessage(CC.translate("&cEste jogador já está mutado."))
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
        MuteManager.mute(target.uniqueId, parsedReason, durationMillis)
        sender.sendMessage(CC.translate("&a${target.name ?: target.uniqueId} foi mutado."))
    }

    @Command(name = "unmute", desc = "Unmute a player")
    @Require("practice.command.mute")
    fun unmute(@Sender sender: CommandSender, target: OfflinePlayer) {
        if (!MuteManager.unmute(target.uniqueId)) {
            sender.sendMessage(CC.translate("&cEste jogador não está mutado."))
            return
        }
        sender.sendMessage(CC.translate("&a${target.name ?: target.uniqueId} foi desmutado."))
    }

    @Command(name = "list", desc = "List muted players")
    @Require("practice.command.mute")
    fun list(@Sender sender: CommandSender) {
        val list = MuteManager.getMutedPlayers()
        sender.sendMessage(CC.translate("&eJogadores mutados: &f${list.size}"))
        list.forEach { uuid ->
            val name = Bukkit.getOfflinePlayer(uuid).name ?: uuid.toString()
            val reason = MuteManager.getReason(uuid) ?: "Sem motivo"
            val exp = MuteManager.getExpiresAt(uuid)
            val timeStr = if (exp != null && exp > 0) " &8(expira em ${formatTime(exp)})" else ""
            sender.sendMessage(CC.translate("&7- &f$name &7(&e$reason&7)$timeStr"))
        }
    }

    private fun parseDuration(input: String): Long {
        if (input.isBlank()) return 0L
        val s = input.trim().lowercase()
        val mult = when {
            s.endsWith("d") -> 24 * 60 * 60 * 1000L
            s.endsWith("h") -> 60 * 60 * 1000L
            s.endsWith("m") -> 60 * 1000L
            s.endsWith("s") -> 1000L
            else -> return 0L
        }
        val num = s.dropLast(1).toLongOrNull() ?: return 0L
        return if (num <= 0) 0L else num * mult
    }

    private fun formatTime(expiresAt: Long): String {
        var rem = (expiresAt - System.currentTimeMillis()).coerceAtLeast(0)
        if (rem == 0L) return "agora"
        val d = rem / (24 * 60 * 60 * 1000)
        rem %= (24 * 60 * 60 * 1000)
        val h = rem / (60 * 60 * 1000)
        rem %= (60 * 60 * 1000)
        val m = rem / (60 * 1000)
        return buildString {
            if (d > 0) append("${d}d ")
            if (h > 0) append("${h}h ")
            append("${m}m")
        }.trim()
    }
}
