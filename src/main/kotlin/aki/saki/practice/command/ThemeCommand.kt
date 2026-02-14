/*
 * This project can be redistributed without
 * authorization of the developer
 *
 * Project @ AkiPractice
 * @author saki © 2026
 */
package aki.saki.practice.command

import aki.saki.practice.PracticePlugin
import aki.saki.practice.theme.ThemeHelper
import aki.saki.practice.ui.ThemeMenu
import aki.saki.practice.utils.CC
import com.jonahseguin.drink.annotation.Command
import org.bukkit.ChatColor
import com.jonahseguin.drink.annotation.Require
import com.jonahseguin.drink.annotation.Sender
import org.bukkit.entity.Player

class ThemeCommand {

    @Command(name = "", desc = "Abre o menu de temas")
    @Require("akipractice.theme.access")
    fun open(@Sender player: Player) {
        ThemeMenu().openMenu(player)
    }

    @Command(name = "set", desc = "Define cor do tema: /theme set <primary|secondary> <cor>")
    @Require("akipractice.theme.access")
    fun set(@Sender player: Player, type: String, color: String) {
        val profile = PracticePlugin.instance.profileManager.findById(player.uniqueId) ?: return
        val kind = type.equals("primary", true) || type.equals("secondary", true)
        if (!kind) {
            player.sendMessage(CC.translate("&cUse: /theme set <primary|secondary> <cor>"))
            return
        }
        if (!ThemeHelper.isValidColor(color)) {
            player.sendMessage(CC.translate("&cCor inválida. Cores: &f${ThemeHelper.getValidColorNames().take(16).joinToString(", ")}"))
            return
        }
        val name = color.uppercase()
        if (type.equals("primary", true)) {
            profile.settings.themePrimary = name
            val c = ChatColor.valueOf(name)
            player.sendMessage(CC.translate("&aCor primária definida para ") + c + name + CC.translate("&a."))
        } else {
            profile.settings.themeSecondary = name
            val c = ChatColor.valueOf(name)
            player.sendMessage(CC.translate("&aCor secundária definida para ") + c + name + CC.translate("&a."))
        }
        profile.save(true)
    }

    @Command(name = "reset", desc = "Reseta tema para o padrão do servidor")
    @Require("akipractice.theme.access")
    fun reset(@Sender player: Player) {
        val profile = PracticePlugin.instance.profileManager.findById(player.uniqueId) ?: return
        profile.settings.themePrimary = null
        profile.settings.themeSecondary = null
        profile.save(true)
        player.sendMessage(CC.translate("&aTema resetado para o padrão do servidor."))
    }
}
