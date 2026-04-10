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
import aki.saki.practice.kit.Kit
import aki.saki.practice.match.ffa.FFA
import java.util.*

object FFAManager {

    val ffaMatches: MutableList<FFA> = mutableListOf()

    fun load() {
        for (kit in PracticePlugin.instance.kitManager.kits.values) {
            if (!kit.ffa) continue

            val ffa = FFA(kit)
            ffaMatches.add(ffa)
        }
    }

    fun getByUUID(uuid: UUID): FFA {
        return ffaMatches.stream().filter { it.uuid == uuid }
            .findFirst().orElse(null)
    }

    fun getByKit(kit: Kit): FFA {
        return ffaMatches.stream().filter { it.kit.name.equals(kit.name, false) }
            .findFirst().orElse(null)
    }
}
