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
import org.bukkit.OfflinePlayer
import org.bukkit.command.CommandSender

class UnbanRankedCommand {

    @Command(name = "", desc = "Remove ranked ban from a player")
    @Require("practice.command.banranked")
    fun unban(@Sender sender: CommandSender, target: OfflinePlayer) {
        if (!RankedBanManager.unban(target.uniqueId)) {
            sender.sendMessage(CC.translate(Locale.RANKED_BAN_NOT_BANNED.getNormalMessage()))
            return
        }
        val msg = Locale.RANKED_BAN_PLAYER_UNBANNED.getNormalMessage().replace("<player>", target.name ?: target.uniqueId.toString())
        sender.sendMessage(CC.translate(msg))
    }
}
