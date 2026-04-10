/*
 * This project can	 be redistributed without
 * authorization of the developer
 *
 * Project @ AkiPractice
 * @author saki © 2026
 * Date: 11/02/2026
 */
package aki.saki.practice.event.player

import org.bukkit.Bukkit
import org.bukkit.entity.Player
import java.util.*


/**
 * This Project is property of Zowpy © 2022
 * Redistribution of this Project is not allowed
 *
 * @author Zowpy
 * Created: 3/8/2022
 * Project: lPractice
 */

class EventPlayer(val uuid: UUID, val name: String) {

    val player: Player
      get() = Bukkit.getPlayer(uuid)

    var dead = false
    var offline = false

    var roundsPlayed = 0

    var state = EventPlayerState.LOBBY

    // tnt tag
    var tagged = false
}
