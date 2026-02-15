/*
 * Project @ AkiPractice
 * @author saki © 2026
 */
package aki.saki.practice.listener

import aki.saki.practice.PracticePlugin
import aki.saki.practice.manager.CampManager
import aki.saki.practice.manager.MuteManager
import aki.saki.practice.utils.CC
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.AsyncPlayerChatEvent

object ChatListener : Listener {

    @EventHandler(priority = EventPriority.LOWEST)
    fun onChat(event: AsyncPlayerChatEvent) {
        if (MuteManager.isMuted(event.player.uniqueId)) {
            event.isCancelled = true
            val reason = MuteManager.getReason(event.player.uniqueId)
            event.player.sendMessage(CC.translate(
                if (reason != null) "&cVocê está mutado. Motivo: &e$reason"
                else "&cVocê está mutado."
            ))
            return
        }
        val profile = PracticePlugin.instance.profileManager.findById(event.player.uniqueId) ?: return
        val campTag = CampManager.getByPlayer(event.player.uniqueId)?.let { "&8[&7${it.tag}&8] " } ?: ""
        val prefix = CC.translate("&7[&eLv.${profile.level}&7] $campTag&f")
        event.format = prefix + "%1\$s&f: %2\$s"
    }
}
