/*
 * Project @ AkiPractice
 * @author saki © 2026
 */
package aki.saki.practice.listener

import aki.saki.practice.PracticePlugin
import aki.saki.practice.manager.CampManager
import aki.saki.practice.manager.MuteManager
import aki.saki.practice.profile.Profile
import aki.saki.practice.utils.CC
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.AsyncPlayerChatEvent

object ChatListener : Listener {

    private fun formatLevelPrefix(profile: Profile): String {
        return when (profile.settings.levelChatStyle?.uppercase()) {
            "MINIMAL" -> "&f${profile.level}❤ "
            "GOLD" -> "&6★${profile.level}&f "
            "BOLD" -> "&e&l[Lv ${profile.level}]&r "
            else -> "&7[&eLv.${profile.level}&7] "
        }
    }

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
        val levelPart = formatLevelPrefix(profile)
        val prefix = CC.translate("$levelPart$campTag&f")
        event.format = "$prefix%1\$s&f: %2\$s"
    }
}
