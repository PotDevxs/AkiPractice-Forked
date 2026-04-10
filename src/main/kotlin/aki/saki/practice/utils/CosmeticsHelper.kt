/*
 * Project @ AkiPractice
 * @author saki © 2026
 */
package aki.saki.practice.utils

import aki.saki.practice.PracticePlugin
import org.bukkit.Effect
import org.bukkit.Location
import org.bukkit.entity.Player

object CosmeticsHelper {

    private fun getKillEffect(): Effect? = parseEffect(PracticePlugin.instance.settingsFile.getString("COSMETICS.KILL-EFFECT") ?: "NONE")
    private fun getVictoryEffect(): Effect? = parseEffect(PracticePlugin.instance.settingsFile.getString("COSMETICS.VICTORY-EFFECT") ?: "NONE")

    private fun parseEffect(name: String): Effect? {
        if (name.equals("NONE", true)) return null
        val key = name.uppercase().replace("FIREWORK", "FIREWORKS_SPARK")
        return try { Effect.valueOf(key) } catch (_: Exception) { null }
    }

    fun playKillEffect(location: Location) {
        val effect = getKillEffect() ?: return
        val world = location.world ?: return
        try {
            repeat(8) { world.playEffect(location, effect, 0) }
        } catch (_: Exception) { }
    }

    fun playVictoryEffect(player: Player) {
        val effect = getVictoryEffect() ?: return
        val loc = player.location
        try {
            repeat(12) { loc.world?.playEffect(loc, effect, 0) }
        } catch (_: Exception) { }
    }
}
