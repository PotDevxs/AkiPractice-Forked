/*
 * This project can	 be redistributed without
 * authorization of the developer
 *
 * Project @ AkiPractice
 * @author saki © 2026
 * Date: 11/02/2026
 */
package aki.saki.practice.command.admin

import com.jonahseguin.drink.annotation.Command
import com.jonahseguin.drink.annotation.Require
import com.jonahseguin.drink.annotation.Sender
import aki.saki.practice.PracticePlugin
import aki.saki.practice.utils.CC
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

class BuildCommand {

    @Command(name = "", desc = "Toggle build mode")
    @Require("practice.command.build")
    fun build(@Sender sender: CommandSender) {
        val player = sender as Player
        val profile = PracticePlugin.instance.profileManager.findById(player.uniqueId)!!

        if (profile.match != null) return

        if (profile.canBuild == true) {
            profile.canBuild = false
            player.sendMessage("${CC.RED}You can no longer build")
        } else {
            profile.canBuild = true
            player.sendMessage("${CC.GREEN}You may now build")
        }
    }
}
