/*
 * Project @ AkiPractice
 * @author saki © 2026
 */
package aki.saki.practice.command

import aki.saki.practice.utils.CC
import aki.saki.practice.utils.PlayerUtil
import com.jonahseguin.drink.annotation.Command
import com.jonahseguin.drink.annotation.Sender
import org.bukkit.entity.Player

class PingCommand {

    @Command(name = "", desc = "Show your or another player's ping")
    fun ping(@Sender player: Player, target: Player? = null) {
        val who = target ?: player
        val ms = PlayerUtil.getPing(who)
        if (who == player) {
            player.sendMessage(CC.translate("&fSeu ping: &a${ms}ms"))
        } else {
            player.sendMessage(CC.translate("&fPing de &e${who.name}&f: &a${ms}ms"))
        }
    }
}
