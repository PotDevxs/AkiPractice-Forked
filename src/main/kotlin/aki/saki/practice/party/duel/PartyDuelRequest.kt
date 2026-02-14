/*
 * This project can	 be redistributed without
 * authorization of the developer
 *
 * Project @ AkiPractice
 * @author saki © 2026
 * Date: 11/02/2026
 */
package aki.saki.practice.party.duel

import aki.saki.practice.arena.Arena
import aki.saki.practice.kit.Kit
import aki.saki.practice.manager.PartyManager
import aki.saki.practice.utils.CC
import aki.saki.practice.utils.TextBuilder
import org.bukkit.Bukkit
import java.util.*


/**
 * This Project is property of Zowpy © 2022
 * Redistribution of this Project is not allowed
 *
 * @author Zowpy
 * Created: 3/3/2022
 * Project: lPractice
 */

class PartyDuelRequest(val partyUUID: UUID, val issuer: UUID) {

    private val executedAt = System.currentTimeMillis()
    var kit: Kit? = null
    var arena: Arena? = null

    fun isExpired(): Boolean {
        return System.currentTimeMillis() - executedAt >= 60_000
    }

    fun send() {

        val party = PartyManager.getByUUID(partyUUID)
        val sender = Bukkit.getPlayer(issuer)
        val leader = Bukkit.getPlayer(party?.leader)

        party?.duelRequests?.add(this)

        val message = TextBuilder()
            .setText("${CC.SECONDARY}${sender.name}${CC.PRIMARY}'s party has sent your party a duel request with kit ${CC.SECONDARY}${kit?.name}${CC.PRIMARY} on")
            .then()
            .setText(" arena ${CC.SECONDARY}${arena?.name}${CC.PRIMARY}.")
            .then()
            .setText("${CC.SECONDARY} [Click to accept]")
            .setCommand("/party accept ${sender.name}")
            .then()
            .build()

        leader.spigot().sendMessage(message)
    }
}
