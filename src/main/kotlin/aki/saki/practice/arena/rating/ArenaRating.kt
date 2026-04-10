/*
 * This project can	 be redistributed without
 * authorization of the developer
 *
 * Project @ AkiPractice
 * @author saki © 2026
 * Date: 11/02/2026
 */
package aki.saki.practice.arena.rating

import aki.saki.practice.PracticePlugin
import org.bson.Document
import java.util.*

/**
 * This Project is property of Zowpy © 2022
 * Redistribution of this Project is not allowed
 *
 * @author Zowpy & EliteAres
 * Created: 6/2/2022
 * Project: lPractice
 */

class ArenaRating(val uuid: UUID, val stars: Int, val user: UUID, val arena: String) {

    private fun toBson(): Document {
        return Document("uuid", uuid.toString())
            .append("stars", stars)
            .append("user", user.toString())
            .append("arena", arena)
    }

    fun save() {
        PracticePlugin.instance.database.arenaRatingUpsert(toBson())
    }
}
