/*
 * This project can	 be redistributed without
 * authorization of the developer
 *
 * Project @ AkiPractice
 * @author saki © 2026
 * Date: 11/02/2026
 */
package aki.saki.practice.queue

import aki.saki.practice.Locale
import org.bukkit.Bukkit
import java.util.*

class QueuePlayer(
    val uuid: UUID,
    val name: String,
    val queue: Queue,
    val pingFactor: Int,
    var elo: Int = 0,
    initialRange: Int = 25,
    private val rangeStep: Int = 5,
    val priority: Boolean = false
) {

    val started: Long = System.currentTimeMillis()
    private var range: Int = initialRange.coerceIn(1, 500)
    private var ticked: Int = 0

    fun tickRange() {
        ticked++
        if (ticked % 3 == 0) {
            range += rangeStep
            if (ticked >= 50) {
                ticked = 0
                if (queue.type == QueueType.RANKED) {
                    Bukkit.getPlayer(uuid)?.sendMessage(Locale.ELO_SEARCH.getMessage()
                        .replace("<min>", "${getMinRange()}")
                        .replace("<max>", "${getMaxRange()}"))
                }
            }
        }
    }

    fun isInRange(elo: Int): Boolean {
        return elo in (this.elo - range)..(this.elo + range)
    }

    fun getMinRange(): Int {
        return (elo - range).coerceAtLeast(0)
    }

    fun getMaxRange(): Int {
        return (elo + range).coerceAtMost(2500)
    }
}
