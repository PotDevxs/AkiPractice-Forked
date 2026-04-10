/*
 * This project can	 be redistributed without
 * authorization of the developer
 *
 * Project @ AkiPractice
 * @author saki © 2026
 * Date: 11/02/2026
 */
package aki.saki.practice.task

import aki.saki.practice.PracticePlugin
import aki.saki.practice.manager.FFAManager
import org.bukkit.scheduler.BukkitRunnable

/*
 * This project can't be redistributed without
 * authorization of the developer
 *
 * Project @ lPractice
 * @author yek4h © 2024
 * Date: 05/06/2024
 */

object FFAItemClearTask : BukkitRunnable() {

    init {
        this.runTaskTimer(PracticePlugin.instance, 20 * 60, 20 * 60)
    }

    override fun run() {
        FFAManager.ffaMatches.forEach { ffa ->
            ffa.droppedItems.forEach { it.remove() }
            ffa.droppedItems.clear()
        }
    }
}
