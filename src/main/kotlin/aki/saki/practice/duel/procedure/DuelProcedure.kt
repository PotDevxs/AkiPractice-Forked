/*
 * This project can	 be redistributed without
 * authorization of the developer
 *
 * Project @ AkiPractice
 * @author saki © 2026
 * Date: 11/02/2026
 */
package aki.saki.practice.duel.procedure

import aki.saki.practice.arena.Arena
import aki.saki.practice.duel.DuelRequest
import aki.saki.practice.kit.Kit
import java.util.*


/**
 * This Project is property of Zowpy © 2022
 * Redistribution of this Project is not allowed
 *
 * @author Zowpy
 * Created: 2/28/2022
 * Project: lPractice
 */

class DuelProcedure(var uuid: UUID, var target: UUID) {

    var stage = DuelProcedureStage.KIT
    var kit: Kit? = null
    var arena: Arena? = null

    fun create(): DuelRequest {
        duelProcedures.remove(this)

        return DuelRequest(uuid, target, kit!!, arena!!)
    }

    companion object {
        @JvmStatic
        val duelProcedures: MutableList<DuelProcedure> = mutableListOf()

        fun getByUUID(uuid: UUID): DuelProcedure? {
            return duelProcedures.stream().filter { it.uuid == uuid }
                .findAny().orElse(null)
        }
    }
}
