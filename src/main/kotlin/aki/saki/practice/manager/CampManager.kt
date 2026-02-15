/*
 * This project can be redistributed without
 * authorization of the developer
 *
 * Project @ AkiPractice
 * @author saki © 2026
 */
package aki.saki.practice.manager

import aki.saki.practice.PracticePlugin
import aki.saki.practice.camp.Camp
import aki.saki.practice.camp.CampInvitation
import com.mongodb.client.model.Filters
import org.bson.Document
import java.util.*

object CampManager {

    val camps: MutableMap<UUID, Camp> = mutableMapOf()
    val pendingInvites: MutableMap<UUID, MutableList<CampInvitation>> = mutableMapOf()

    fun getByUUID(uuid: UUID): Camp? = camps[uuid]

    fun getByPlayer(playerUUID: UUID): Camp? =
        camps.values.firstOrNull { it.isMember(playerUUID) }

    fun getByName(name: String): Camp? =
        camps.values.firstOrNull { it.name.equals(name, ignoreCase = true) }

    fun getByTag(tag: String): Camp? =
        camps.values.firstOrNull { it.tag.equals(tag, ignoreCase = true) }

    fun getInvitation(target: UUID, campUUID: UUID): CampInvitation? =
        pendingInvites[target]?.firstOrNull { it.campUUID == campUUID && !it.isExpired() }

    fun create(name: String, tag: String, leader: UUID): Camp? {
        if (getByPlayer(leader) != null) return null
        if (getByName(name) != null || getByTag(tag) != null) return null
        val camp = Camp(UUID.randomUUID(), name, tag.uppercase().take(5), leader)
        camps[camp.uuid] = camp
        saveCamp(camp)
        return camp
    }

    fun remove(camp: Camp) {
        camp.members.toList().forEach { camp.members.remove(it) }
        camps.remove(camp.uuid)
        deleteCamp(camp.uuid)
    }

    fun saveCamp(camp: Camp) {
        try {
            val doc = Document("_id", camp.uuid.toString())
                .append("name", camp.name)
                .append("tag", camp.tag)
                .append("leader", camp.leader.toString())
                .append("members", camp.members.map { it.toString() })
                .append("totalWins", camp.totalWins)
            PracticePlugin.instance.mongoManager.campCollection.replaceOne(
                Filters.eq("_id", camp.uuid.toString()),
                doc,
                com.mongodb.client.model.UpdateOptions().upsert(true)
            )
        } catch (_: Exception) { }
    }

    private fun deleteCamp(uuid: UUID) {
        try {
            PracticePlugin.instance.mongoManager.campCollection.deleteOne(Filters.eq("_id", uuid.toString()))
        } catch (_: Exception) { }
    }

    fun load() {
        camps.clear()
        try {
            PracticePlugin.instance.mongoManager.campCollection.find().forEach { doc ->
                val uuid = UUID.fromString(doc.getString("_id"))
                val name = doc.getString("name") ?: return@forEach
                val tag = doc.getString("tag") ?: return@forEach
                val leader = UUID.fromString(doc.getString("leader") ?: return@forEach)
                val members = (doc.getList("members", String::class.java) ?: emptyList()).map { UUID.fromString(it) }.toMutableList()
                if (members.isEmpty()) members.add(leader)
                val camp = Camp(uuid, name, tag, leader)
                camp.members.clear()
                camp.members.addAll(members)
                camp.totalWins = doc.getInteger("totalWins", 0)
                camps[uuid] = camp
            }
        } catch (_: Exception) { }
    }
}
