/*
 * This project can	 be redistributed without
 * authorization of the developer
 *
 * Project @ AkiPractice
 * @author saki © 2026
 */
package aki.saki.practice.menus

import aki.saki.practice.PracticePlugin
import aki.saki.practice.menu.Button
import aki.saki.practice.menu.pagination.PaginatedMenu
import aki.saki.practice.utils.CC
import aki.saki.practice.utils.ItemBuilder
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.inventory.ItemStack

class OnlinePlayersLevelMenu : PaginatedMenu() {

    override fun getPrePaginatedTitle(player: Player): String =
        CC.translate("&cJogadores online — nível/XP")

    override fun getAllPagesButtons(player: Player): MutableMap<Int, Button> {
        val map = linkedMapOf<Int, Button>()
        var i = 0
        for (p in Bukkit.getOnlinePlayers().sortedBy { it.name.lowercase() }) {
            map[i++] = object : Button() {
                override fun getButtonItem(pl: Player): ItemStack =
                    ItemBuilder(Material.SKULL_ITEM)
                        .durability(3)
                        .name("&f${p.name}")
                        .lore(
                            "&7Nível: &f${PracticePlugin.instance.profileManager.findById(p.uniqueId)?.level ?: 0}",
                            "&7Clique para editar XP/nível."
                        )
                        .build()

                override fun clicked(pl: Player, slot: Int, clickType: ClickType, hotbarButton: Int) {
                    if (!p.isOnline) {
                        pl.sendMessage(CC.translate("&cJogador offline."))
                        return
                    }
                    AdminTargetLevelMenu(p).openMenu(pl)
                }
            }
        }
        return map
    }
}
