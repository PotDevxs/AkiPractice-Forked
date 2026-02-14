/*
 * This project can be redistributed without
 * authorization of the developer
 *
 * Project @ AkiPractice
 * @author saki © 2026
 */
package aki.saki.practice.ui

import aki.saki.practice.PracticePlugin
import aki.saki.practice.theme.ThemeHelper
import aki.saki.practice.utils.CC
import aki.saki.practice.utils.ItemBuilder
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.inventory.ItemStack
import rip.katz.api.menu.Button
import rip.katz.api.menu.Menu

class ThemeMenu : Menu() {

    override fun getTitle(p0: Player?): String = "Tema (Cores)"

    override fun getButtons(player: Player): MutableMap<Int, Button> {
        val toReturn = mutableMapOf<Int, Button>()
        val profile = PracticePlugin.instance.profileManager.findById(player.uniqueId)!!
        val settings = profile.settings
        val primary = settings.themePrimary ?: "Padrão"
        val secondary = settings.themeSecondary ?: "Padrão"

        toReturn[4] = object : Button() {
            override fun getButtonItem(p0: Player?): ItemStack = ItemBuilder(Material.NAME_TAG)
                .name("${ThemeHelper.getPrimary(player)}Cor primária: ${CC.GRAY}$primary")
                .lore("${CC.GRAY}Clique em uma cor abaixo para definir.")
                .build()
            override fun clicked(player: Player?, slot: Int, clickType: ClickType?, hotbarButton: Int) {}
            override fun shouldUpdate(player: Player?, slot: Int, clickType: ClickType?): Boolean = false
        }

        toReturn[13] = object : Button() {
            override fun getButtonItem(p0: Player?): ItemStack = ItemBuilder(Material.PAPER)
                .name("${ThemeHelper.getSecondary(player)}Cor secundária: ${CC.GRAY}$secondary")
                .lore("${CC.GRAY}Clique em uma cor abaixo para definir.")
                .build()
            override fun clicked(player: Player?, slot: Int, clickType: ClickType?, hotbarButton: Int) {}
            override fun shouldUpdate(player: Player?, slot: Int, clickType: ClickType?): Boolean = false
        }

        val colors = listOf(
            ChatColor.WHITE to Material.WOOL,
            ChatColor.GOLD to Material.WOOL,
            ChatColor.LIGHT_PURPLE to Material.WOOL,
            ChatColor.AQUA to Material.WOOL,
            ChatColor.YELLOW to Material.WOOL,
            ChatColor.GREEN to Material.WOOL,
            ChatColor.RED to Material.WOOL,
            ChatColor.BLUE to Material.WOOL,
            ChatColor.DARK_GRAY to Material.WOOL
        ).mapIndexed { index, (color, mat) ->
            val data = when (color) {
                ChatColor.WHITE -> 0
                ChatColor.GOLD -> 1
                ChatColor.LIGHT_PURPLE -> 2
                ChatColor.AQUA -> 3
                ChatColor.YELLOW -> 4
                ChatColor.GREEN -> 5
                ChatColor.RED -> 14
                ChatColor.BLUE -> 11
                ChatColor.DARK_GRAY -> 7
                else -> 0
            }
            Triple(color, mat, data.toByte())
        }

        colors.forEachIndexed { index, (color, material, data) ->
            val slot = 18 + index
            toReturn[slot] = object : Button() {
                override fun getButtonItem(p0: Player?): ItemStack = ItemBuilder(material)
                    .durability(data.toInt())
                    .name("${color}${color.name}")
                    .lore(
                        "${CC.GRAY}Primária: Clique esquerdo",
                        "${CC.GRAY}Secundária: Clique direito"
                    )
                    .build()
                override fun clicked(p: Player?, slot: Int, clickType: ClickType?, hotbarButton: Int) {
                    if (p == null) return
                    val prof = PracticePlugin.instance.profileManager.findById(p.uniqueId)!!
                    val name = color.name
                    if (clickType?.isLeftClick == true) {
                        prof.settings.themePrimary = name
                        p.sendMessage(CC.translate("&aCor primária: ${color}$name"))
                    } else {
                        prof.settings.themeSecondary = name
                        p.sendMessage(CC.translate("&aCor secundária: ${color}$name"))
                    }
                    prof.save(true)
                }
                override fun shouldUpdate(player: Player?, slot: Int, clickType: ClickType?): Boolean = true
            }
        }

        toReturn[31] = object : Button() {
            override fun getButtonItem(p0: Player?): ItemStack = ItemBuilder(Material.BARRIER)
                .name("${CC.RED}Resetar tema")
                .lore("${CC.GRAY}Voltar às cores padrão do servidor")
                .build()
            override fun clicked(p: Player?, slot: Int, clickType: ClickType?, hotbarButton: Int) {
                if (p == null) return
                val prof = PracticePlugin.instance.profileManager.findById(p.uniqueId)!!
                prof.settings.themePrimary = null
                prof.settings.themeSecondary = null
                prof.save(true)
                p.sendMessage(CC.translate("&aTema resetado."))
            }
            override fun shouldUpdate(player: Player?, slot: Int, clickType: ClickType?): Boolean = true
        }

        return toReturn
    }
}
