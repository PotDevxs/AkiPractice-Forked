/*
 * This project can	 be redistributed without
 * authorization of the developer
 *
 * Project @ AkiPractice
 * @author saki © 2026
 * Date: 11/02/2026
 */
package aki.saki.practice.command

import com.jonahseguin.drink.annotation.Command
import com.jonahseguin.drink.annotation.Require
import com.jonahseguin.drink.annotation.Sender
import aki.saki.practice.Locale
import aki.saki.practice.arena.Arena
import aki.saki.practice.arena.rating.ArenaRating
import aki.saki.practice.manager.ArenaRatingManager
import aki.saki.practice.PracticePlugin
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import java.util.*

/*
 * This project can't be redistributed without
 * authorization of the developer
 *
 * Project @ lPractice
 * @author yek4h © 2024
 * Date: 17/06/2024
*/

class RateMapCommand {

    @Command(name = "", desc = "Rate a map")
    fun rate(@Sender sender: CommandSender, arena: Arena, int: Int) {
        val player = sender as Player
        val profile = PracticePlugin.instance.profileManager.findById(player.uniqueId)!!

        if (profile.settings?.mapRating != true) {
            player.sendMessage(Locale.DISABLED_MAP_RATING.getMessage())
            return
        }

        if (ArenaRatingManager.hasRated(player.uniqueId, arena)) {
            player.sendMessage(Locale.ALREADY_RATED.getMessage())
            return
        }

        val rating = ArenaRating(UUID.randomUUID(), int, player.uniqueId, arena.name)
        rating.save()

        ArenaRatingManager.arenaRatings.add(rating)
        player.sendMessage(Locale.THANK_YOU.getMessage())
    }
}
