/*
 * This project can be redistributed without
 * authorization of the developer
 *
 * Project @ AkiPractice
 * @author saki © 2026
 */
package aki.saki.practice.camp

import aki.saki.practice.utils.CC
import org.bukkit.Bukkit
import java.util.*

/**
 * Camp (clan/guild) — grupo permanente com nome e tag.
 * Diferente de Party: persiste, tem nome/tag, pode ter leaderboard próprio no futuro.
 */
class Camp(val uuid: UUID, var name: String, var tag: String, val leader: UUID) {

    val members: MutableList<UUID> = mutableListOf(leader)
    val createdAt: Long = System.currentTimeMillis()
    var totalWins: Int = 0

    fun sendMessage(message: String) {
        members.forEach { id ->
            Bukkit.getPlayer(id)?.sendMessage(CC.translate(message))
        }
    }

    fun isLeader(uuid: UUID): Boolean = leader == uuid
    fun isMember(uuid: UUID): Boolean = members.contains(uuid)
}
