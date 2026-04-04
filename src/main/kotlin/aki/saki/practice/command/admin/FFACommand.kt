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
import aki.saki.practice.utils.Cuboid
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

class FFACommand {

    @Command(name = "", desc = "FFA arena setup commands")
    @Require("practice.command.ffa.setup")
    fun help(@Sender sender: CommandSender) {
        sender.sendMessage("""
            ${CC.PRIMARY}Comandos de FFA:
            ${CC.SECONDARY}/ffa spawn - Define o spawn da arena de FFA
            ${CC.SECONDARY}/ffa min - Define o ponto mínimo da safezone do FFA
            ${CC.SECONDARY}/ffa max - Define o ponto máximo da safezone do FFA
        """.trimIndent())
    }

    @Command(name = "spawn", desc = "Set the spawn of the FFA arena", aliases = ["setspawn"])
    @Require("practice.command.ffa.setup.spawn")
    fun setSpawn(@Sender sender: CommandSender) {
        val player = sender as? Player ?: return
        Constants.FFA_SPAWN = player.location
        PracticePlugin.instance.ffaFile.config.set("SPAWN", LocationUtil.serialize(player.location))
        PracticePlugin.instance.ffaFile.save()
        player.sendMessage("${CC.GREEN}Spawn do FFA definido com sucesso!")
    }

    @Command(name = "min", desc = "Set the minimum point of the FFA safezone")
    @Require("practice.command.ffa.setup.min")
    fun setMin(@Sender sender: CommandSender) {
        val player = sender as? Player ?: return
        Constants.MIN = player.location
        PracticePlugin.instance.ffaFile.config.set("SAFE-ZONE.MIN", LocationUtil.serialize(player.location))
        PracticePlugin.instance.ffaFile.save()
        player.sendMessage("${CC.GREEN}Localização mínima do FFA definida com sucesso!")
        updateSafeZone()
    }

    @Command(name = "max", desc = "Set the maximum point of the FFA safezone")
    @Require("practice.command.ffa.setup.max")
    fun setMax(@Sender sender: CommandSender) {
        val player = sender as? Player ?: return
        Constants.MAX = player.location
        PracticePlugin.instance.ffaFile.config.set("SAFE-ZONE.MAX", LocationUtil.serialize(player.location))
        PracticePlugin.instance.ffaFile.save()
        player.sendMessage("${CC.GREEN}Localização máxima do FFA definida com sucesso!")
        updateSafeZone()
    }

    private fun updateSafeZone() {
        if (Constants.MIN != null && Constants.MAX != null) {
            Constants.SAFE_ZONE = Cuboid(Constants.MIN!!, Constants.MAX!!)
        }
    }
}
