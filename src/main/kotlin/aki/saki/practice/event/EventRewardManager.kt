/*
 * Project @ AkiPractice
 * @author saki © 2026
 */
package aki.saki.practice.event

import aki.saki.practice.PracticePlugin
import aki.saki.practice.event.player.EventPlayer
import aki.saki.practice.utils.CC
import org.bukkit.Bukkit

object EventRewardManager {

    /**
     * Gives the event winner the configured XP reward and notifies them.
     * Call this when the event ends with a winner.
     */
    fun giveWinnerReward(event: Event, winner: EventPlayer?) {
        if (winner == null) return
        val profile = PracticePlugin.instance.profileManager.findById(winner.uuid) ?: return
        val settings = PracticePlugin.instance.settingsFile
        val xp = settings.getInt("EVENT.REWARD-XP", 50).toLong()
        if (xp <= 0) return
        profile.addXp(xp)
        profile.save(true)
        val player = Bukkit.getPlayer(winner.uuid) ?: return
        player.sendMessage(CC.translate("&a&lVitória no evento! &f+$xp XP."))
    }
}
