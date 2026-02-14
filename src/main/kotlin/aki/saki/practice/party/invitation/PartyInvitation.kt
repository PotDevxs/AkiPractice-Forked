/*
 * This project can	 be redistributed without
 * authorization of the developer
 *
 * Project @ AkiPractice
 * @author saki © 2026
 * Date: 11/02/2026
 */
package aki.saki.practice.party.invitation

import java.util.*


/**
 * This Project is property of Zowpy © 2022
 * Redistribution of this Project is not allowed
 *
 * @author Zowpy
 * Created: 2/22/2022
 * Project: lPractice
 */

class PartyInvitation(val uuid: UUID, val player: UUID) {

    val executedAt = System.currentTimeMillis()

    fun isExpired(): Boolean {
        return System.currentTimeMillis() - executedAt >= 60000 * 5
    }
}
