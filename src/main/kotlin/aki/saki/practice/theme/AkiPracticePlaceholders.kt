package aki.saki.practice.theme

import aki.saki.practice.PracticePlugin
import me.clip.placeholderapi.expansion.PlaceholderExpansion
import org.bukkit.OfflinePlayer

class AkiPracticePlaceholders : PlaceholderExpansion() {

    override fun getIdentifier(): String = "akipractice"
    override fun getAuthor(): String = "AkiPractice"
    override fun getVersion(): String = "1.0.0"

    override fun persist(): Boolean = true

    override fun onRequest(player: OfflinePlayer?, params: String): String {
        if (player == null || !player.isOnline) return ""
        val p = player.player ?: return ""
        return when (params.lowercase()) {
            "theme" -> ThemeHelper.getPrimary(p)
            else -> ""
        }
    }
}
