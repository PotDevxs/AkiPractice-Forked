/*
 * This project can	 be redistributed without
 * authorization of the developer
 *
 * Project @ AkiPractice
 * @author saki © 2026
 * Date: 11/02/2026
 */
package aki.saki.practice.ui.kit.buttons.misc

import aki.saki.practice.PracticePlugin
import aki.saki.practice.kit.Kit
import aki.saki.practice.ui.queue.unranked.UnrankedQueueMenu
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.inventory.ItemStack
import rip.katz.api.menu.Button
import rip.katz.api.utils.ItemBuilder


/*
 * This project can't be redistributed without
 * authorization of the developer
 *
 * Project @ lPractice
 * @author yek4h © 2024
 * Date: 13/06/2024
*/

class KitQueuePosButton(
    val kit: Kit
) : Button() {

    override fun getButtonItem(player: Player?): ItemStack {
        val unrankedOrNot = PracticePlugin.instance.kitManager.unrankedOrNot
        val positionType = if (unrankedOrNot) "&aUnranked" else "&cRanked"
        val position = if (unrankedOrNot) kit.unrankedPosition else kit.rankedPosition
        return ItemBuilder(Material.NETHER_STAR)
            .name("&b&lQueue Position Editor &7($positionType&7)")
            .lore(listOf(
                "",
                "&7Left-Click to add a value",
                "&7Right-Click to subtract a value",
                "&7Shift-Click to switch between Unranked and Ranked Menu position",
                "",
                "&cWHEN YOU MODIFY THIS VALUE YOU NEED TO RESTART",
                "&cTHE SERVER TO APPLY CHANGES!",
                "",
                "${if (unrankedOrNot) "&aUnranked" else "&cRanked"} &eMenu Position: &f$position",
                ""
            ))
            .build()
    }

    override fun clicked(player: Player, slot: Int, clickType: ClickType, hotbarButton: Int) {
        when (clickType) {
            ClickType.MIDDLE -> {
                PracticePlugin.instance.kitManager.unrankedOrNot = !PracticePlugin.instance.kitManager.unrankedOrNot
            }
            ClickType.LEFT -> {
                if (PracticePlugin.instance.kitManager.unrankedOrNot) kit.unrankedPosition++ else kit.rankedPosition++
            }
            ClickType.RIGHT -> {
                if (PracticePlugin.instance.kitManager.unrankedOrNot && kit.unrankedPosition > 0) {
                    kit.unrankedPosition--
                } else if (!PracticePlugin.instance.kitManager.unrankedOrNot && kit.rankedPosition > 0) {
                    kit.rankedPosition--
                }
            }
            else -> {}
        }
    }
}
