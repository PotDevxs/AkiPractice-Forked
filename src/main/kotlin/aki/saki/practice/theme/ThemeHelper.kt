package aki.saki.practice.theme

import aki.saki.practice.PracticePlugin
import aki.saki.practice.manager.CampManager
import aki.saki.practice.utils.CC
import org.bukkit.ChatColor
import org.bukkit.entity.Player

object ThemeHelper {

    const val PERMISSION = "akipractice.theme.access"

    /** Placeholder único: cor primária do tema do jogador. Use <theme> em scoreboard/mensagens. */
    const val PLACEHOLDER_THEME = "<theme>"

    private val validColorNames = listOf(
        "BLACK", "DARK_BLUE", "DARK_GREEN", "DARK_AQUA", "DARK_RED", "DARK_PURPLE",
        "GOLD", "GRAY", "DARK_GRAY", "BLUE", "GREEN", "AQUA", "RED", "LIGHT_PURPLE", "YELLOW", "WHITE"
    )

    fun hasAccess(player: Player): Boolean = player.hasPermission(PERMISSION)

    fun getPrimary(player: Player): String {
        if (!hasAccess(player)) return CC.PRIMARY
        val profile = PracticePlugin.instance.profileManager.findById(player.uniqueId) ?: return CC.PRIMARY
        val name = profile.settings.themePrimary ?: return CC.PRIMARY
        return safeColor(name) ?: CC.PRIMARY
    }

    fun getSecondary(player: Player): String {
        if (!hasAccess(player)) return CC.SECONDARY
        val profile = PracticePlugin.instance.profileManager.findById(player.uniqueId) ?: return CC.SECONDARY
        val name = profile.settings.themeSecondary ?: return CC.SECONDARY
        return safeColor(name) ?: CC.SECONDARY
    }

    private fun safeColor(name: String): String? {
        return try {
            ChatColor.valueOf(name.uppercase()).toString()
        } catch (_: Exception) {
            null
        }
    }

    fun replacePlaceholders(text: String, player: Player): String {
        val profile = PracticePlugin.instance.profileManager.findById(player.uniqueId)
        val campTag = CampManager.getByPlayer(player.uniqueId)?.let { "[${it.tag}] " } ?: ""
        var result = text.replace(PLACEHOLDER_THEME, getPrimary(player)).replace("<camp>", campTag)
        if (profile != null) {
            result = result.replace("<level>", profile.level.toString()).replace("<xp>", profile.xp.toString())
        }
        return result
    }

    fun replacePlaceholders(lines: List<String>, player: Player): List<String> =
        lines.map { replacePlaceholders(it, player) }

    fun getValidColorNames(): List<String> = validColorNames

    fun isValidColor(name: String): Boolean = name.uppercase() in validColorNames
}
