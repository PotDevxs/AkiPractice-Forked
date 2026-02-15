/*
 * Project @ AkiPractice
 * @author saki © 2026
 */
package aki.saki.practice.manager

import aki.saki.practice.PracticePlugin
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

object MatchLogManager {

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
    private val file: File get() = File(PracticePlugin.instance.dataFolder, "matches.log")

    /**
     * Log a finished 1v1 match for replay/anti-cheat. Appends one line per match.
     */
    fun log(
        matchId: UUID,
        kitName: String,
        winnerUuid: UUID,
        winnerName: String,
        loserUuid: UUID,
        loserName: String,
        ranked: Boolean,
        durationMs: Long
    ) {
        val ts = dateFormat.format(Date())
        val line = "$ts | $matchId | $kitName | $winnerUuid | $winnerName | $loserUuid | $loserName | ranked=$ranked | ${durationMs}ms\n"
        try {
            file.parentFile?.mkdirs()
            file.appendText(line)
        } catch (_: Exception) { }
    }
}
