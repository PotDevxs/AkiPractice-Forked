/*
 * This project can	 be redistributed without
 * authorization of the developer
 *
 * Project @ AkiPractice
 * @author saki © 2026
 * Date: 11/02/2026
 */
package aki.saki.practice.match.player

import lombok.Getter
import org.bukkit.Location
import java.util.*

/**
 * This Project is property of Zowpy © 2021
 * Redistribution of this Project is not allowed
 *
 * @author Zowpy
 * Created: 12/19/2021
 * Project: Practice
 */
@Getter
class TeamMatchPlayer(uuid: UUID, name: String, spawn: Location, val teamUniqueId: UUID, initialElo: Int) : MatchPlayer(uuid, name, spawn, initialElo)
