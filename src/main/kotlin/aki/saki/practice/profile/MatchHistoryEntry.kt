/*
 * Project @ AkiPractice
 * @author saki © 2026
 */
package aki.saki.practice.profile

import java.util.*

data class MatchHistoryEntry(
    val opponentId: UUID,
    val opponentName: String,
    val kitName: String,
    val won: Boolean,
    val ranked: Boolean,
    val time: Long = System.currentTimeMillis()
)
