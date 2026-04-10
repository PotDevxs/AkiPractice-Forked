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
import aki.saki.practice.profile.ProfileState
import org.bukkit.scheduler.BukkitRunnable

/*
 * This project can't be redistributed without
 * authorization of the developer
 *
 * Project @ lPractice
 * @author yek4h © 2024
 * Date: 05/06/2024
 */

object EnderPearlCooldownTask: BukkitRunnable() {

    init {
        this.runTaskTimerAsynchronously(PracticePlugin.instance, 2L, 2L)
    }

    override fun run() {
        for (profile in PracticePlugin.instance.profileManager.profiles.values) {
            if (profile?.state == ProfileState.MATCH || profile?.state == ProfileState.EVENT || profile?.state == ProfileState.FFA) {
                if (profile.enderPearlCooldown != null && !profile.enderPearlCooldown!!.hasExpired()) {
                    val player = profile.player
                    val seconds = profile.enderPearlCooldown!!.timeRemaining.toInt() / 1000

                    player.level = seconds
                    player.exp = profile.enderPearlCooldown!!.timeRemaining / 16000.0f
                }
            }
        }
    }
}
