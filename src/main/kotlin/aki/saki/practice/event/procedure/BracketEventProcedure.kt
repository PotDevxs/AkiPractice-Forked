/*
 * This project can	 be redistributed without
 * authorization of the developer
 *
 * Project @ AkiPractice
 * @author saki © 2026
 * Date: 11/02/2026
 */
package aki.saki.practice.event.procedure

import aki.saki.practice.event.map.EventMap
import aki.saki.practice.kit.Kit
import java.util.*


/**
 * This Project is property of Zowpy © 2022
 * Redistribution of this Project is not allowed
 *
 * @author Zowpy
 * Created: 3/21/2022
 * Project: lPractice
 */

class BracketEventProcedure(var uuid: UUID, var eventMap: EventMap) {

    var kit: Kit? = null

    companion object {

        @JvmStatic
        val procedures: MutableMap<UUID, BracketEventProcedure> = mutableMapOf()
    }
}
