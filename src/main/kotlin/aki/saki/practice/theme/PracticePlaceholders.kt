/*
 * Project @ AkiPractice
 * @author saki © 2026
 *
 * Placeholders %practice_*% (alias do identificador "practice").
 */
package aki.saki.practice.theme

import me.clip.placeholderapi.expansion.PlaceholderExpansion
import org.bukkit.OfflinePlayer

class PracticePlaceholders : PlaceholderExpansion() {

    override fun getIdentifier(): String = "practice"
    override fun getAuthor(): String = "AkiPractice"
    override fun getVersion(): String = "1.0.0"

    override fun persist(): Boolean = true

    override fun onRequest(player: OfflinePlayer?, params: String): String =
        PracticePlaceholderResolver.resolve(player, params)
}
