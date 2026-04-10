/*
 * Project @ AkiPractice
 * @author saki © 2026
 */
package aki.saki.practice.camp

import java.util.*

class CampInvitation(
    val campUUID: UUID,
    val inviter: UUID,
    val target: UUID,
    val createdAt: Long = System.currentTimeMillis()
) {
    fun isExpired(): Boolean = System.currentTimeMillis() - createdAt >= 60_000
}
