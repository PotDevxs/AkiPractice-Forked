/*
 * Project @ AkiPractice
 * @author saki © 2026
 */
package aki.saki.practice.command

import aki.saki.practice.mission.MissionManager
import com.jonahseguin.drink.annotation.Command
import com.jonahseguin.drink.annotation.Sender
import org.bukkit.entity.Player

class MissionCommand {

    @Command(name = "", desc = "View your daily mission")
    fun mission(@Sender player: Player) {
        MissionManager.sendMissionBookMessage(player)
    }
}
