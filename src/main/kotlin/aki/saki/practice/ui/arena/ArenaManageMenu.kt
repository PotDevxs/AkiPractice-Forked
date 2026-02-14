/*
 * This project can	 be redistributed without
 * authorization of the developer
 *
 * Project @ AkiPractice
 * @author saki © 2026
 * Date: 11/02/2026
 */
package aki.saki.practice.ui.arena

import rip.katz.api.menu.Menu
import rip.katz.api.menu.Button
import aki.saki.practice.arena.Arena
import aki.saki.practice.utils.ItemBuilder
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.inventory.ItemStack


/**
 * This Project is property of Zowpy © 2022
 * Redistribution of this Project is not allowed
 *
 * @author Zowpy
 * Created: 2/24/2022
 * Project: lPractice
 */

class ArenaManageMenu(private val arena: Arena): Menu() {

    override fun getTitle(p0: Player?): String {
        return "Arena Management"
    }

    override fun getButtons(player: Player?): MutableMap<Int, Button> {
        val toReturn: MutableMap<Int, Button> = mutableMapOf()

        toReturn[0] = object : Button() {

            override fun getButtonItem(p0: Player?): ItemStack {
                return ItemBuilder(Material.PAPER)
                    .name("&eDuplicates")
                    .lore(listOf(
                        "&e&o(( left click to view ))",
                        "&e&o(( right click to duplicate ))"
                    )).build()
            }

            override fun clicked(player: Player?, slot: Int, clickType: ClickType?, hotbarButton: Int) {
                if (clickType?.isLeftClick!!) {

                }
            }
        }

        return toReturn
    }
}
