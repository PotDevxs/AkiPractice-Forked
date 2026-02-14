/*
 * This project can	 be redistributed without
 * authorization of the developer
 *
 * Project @ AkiPractice
 * @author saki © 2026
 * Date: 11/02/2026
 */
package aki.saki.practice.manager

import aki.saki.practice.PracticePlugin
import aki.saki.practice.arena.Arena
import aki.saki.practice.arena.rating.ArenaRating
import java.util.*

object ArenaRatingManager {

    val arenaRatings: MutableList<ArenaRating> = mutableListOf()

    fun load() {
        val ratingsCollection = PracticePlugin.instance.mongoManager.arenaRatingsCollection
        val documents = ratingsCollection.find().toList()

        if (documents.isEmpty()) return

        documents.forEach { document ->
            val arenaRating = ArenaRating(
                UUID.fromString(document.getString("uuid")),
                document.getInteger("stars"),
                UUID.fromString(document.getString("user")),
                document.getString("arena")
            )
            arenaRatings.add(arenaRating)
        }
    }

    fun getArenaRatings(arena: Arena): List<ArenaRating> {
        return arenaRatings.filter { it.arena.equals(arena.name, true) }
    }

    fun hasRated(uuid: UUID, arena: Arena): Boolean {
        return getArenaRatings(arena).any { it.user == uuid }
    }

    fun getAverageRating(arena: Arena): Double {
        val ratings = getArenaRatings(arena)
        val totalRatings = ratings.sumOf { it.stars }

        return if (ratings.isNotEmpty()) totalRatings.toDouble() / ratings.size else 0.0
    }

    fun getUsersRated(stars: Int, arena: Arena): Int {
        return getArenaRatings(arena).count { it.stars == stars }
    }
}
