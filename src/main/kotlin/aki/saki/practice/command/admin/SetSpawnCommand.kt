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
import aki.saki.practice.constants.Constants
import aki.saki.practice.utils.CC
import aki.saki.practice.utils.LocationUtil
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

class SetSpawnCommand {

    @Command(name = "", desc = "Set the spawn location")
    @Require("practice.admin")
    fun setSpawn(@Sender sender: CommandSender) {
        val player = sender as? Player ?: return
        Constants.SPAWN = player.location
        PracticePlugin.instance.settingsFile.config.set("SPAWN", LocationUtil.serialize(player.location))
        PracticePlugin.instance.settingsFile.save()

        player.sendMessage("${CC.GREEN}Successfully set spawn!")
    }
}
