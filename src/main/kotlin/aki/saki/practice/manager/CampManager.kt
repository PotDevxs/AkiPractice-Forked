/*
 * This project can be redistributed without
 * authorization of the developer
 *
 * Project @ AkiPractice
 * @author saki © 2026
 */
package aki.saki.practice.manager

import aki.saki.practice.camp.Camp
import java.util.*

object CampManager {

    val camps: MutableMap<UUID, Camp> = mutableMapOf()

    fun getByUUID(uuid: UUID): Camp? = camps[uuid]

    fun getByPlayer(playerUUID: UUID): Camp? =
        camps.values.firstOrNull { it.isMember(playerUUID) }

    fun getByName(name: String): Camp? =
        camps.values.firstOrNull { it.name.equals(name, ignoreCase = true) }

    fun getByTag(tag: String): Camp? =
        camps.values.firstOrNull { it.tag.equals(tag, ignoreCase = true) }

    fun create(name: String, tag: String, leader: UUID): Camp? {
        if (getByPlayer(leader) != null) return null
        if (getByName(name) != null || getByTag(tag) != null) return null
        val camp = Camp(UUID.randomUUID(), name, tag.uppercase().take(5), leader)
        camps[camp.uuid] = camp
        return camp
    }

    fun remove(camp: Camp) {
        camp.members.toList().forEach { camp.members.remove(it) }
        camps.remove(camp.uuid)
    }
}
