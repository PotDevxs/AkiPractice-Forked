/*
 * This project can	 be redistributed without
 * authorization of the developer
 *
 * Project @ AkiPractice
 * @author saki © 2026
 * Date: 11/02/2026
 */
package aki.saki.practice.manager

import aki.saki.practice.party.Party
import java.util.*

object PartyManager {

    val parties: MutableList<Party> = mutableListOf()

    fun getByUUID(uuid: UUID): Party? {
        return parties.stream().filter { it.uuid == uuid }
            .findFirst().orElse(null)
    }
}
