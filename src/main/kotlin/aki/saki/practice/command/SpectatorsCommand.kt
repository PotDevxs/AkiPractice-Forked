/*
 * Project @ AkiPractice
 * @author saki © 2026
 */
package aki.saki.practice.command

import aki.saki.practice.PracticePlugin
import aki.saki.practice.match.Match
import aki.saki.practice.profile.ProfileState
import aki.saki.practice.utils.CC
import com.jonahseguin.drink.annotation.Command
import com.jonahseguin.drink.annotation.Sender
import org.bukkit.entity.Player

class SpectatorsCommand {

    @Command(name = "", desc = "List who is spectating your match")
    fun spectators(@Sender player: Player) {
        val profile = PracticePlugin.instance.profileManager.findById(player.uniqueId) ?: return
        if (profile.state != ProfileState.MATCH || profile.match == null) {
            player.sendMessage(CC.translate("&cVocê não está em uma partida."))
            return
        }
        val match = Match.getByUUID(profile.match!!) ?: run {
            player.sendMessage(CC.translate("&cPartida não encontrada."))
            return
        }
        val list = match.spectators.map { it.player.name }.filterNotNull()
        if (list.isEmpty()) {
            player.sendMessage(CC.translate("&eNinguém está assistindo sua partida."))
            return
        }
        player.sendMessage(CC.translate("&eEspectadores (&f${list.size}&e): &a${list.joinToString("&7, &a")}"))
    }
}
