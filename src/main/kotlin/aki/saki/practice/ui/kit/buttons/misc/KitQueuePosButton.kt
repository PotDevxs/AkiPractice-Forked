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
import aki.saki.practice.menu.Button
import aki.saki.practice.utils.ItemBuilder


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

    override fun getButtonItem(player: Player): ItemStack {
        val unrankedOrNot = PracticePlugin.instance.kitManager.unrankedOrNot
        val positionType = if (unrankedOrNot) "&aCasual" else "&cCompetitivo"
        val position = if (unrankedOrNot) kit.unrankedPosition else kit.rankedPosition
        return ItemBuilder(Material.NETHER_STAR)
            .name("&b&lEditor de posição da fila &7($positionType&7)")
            .lore(listOf(
                "",
                "&7Clique esquerdo para adicionar um valor",
                "&7Clique direito para diminuir um valor",
                "&7Shift + clique para alternar entre a posição do menu casual e competitivo",
                "",
                "&cQUANDO VOCÊ ALTERAR ESTE VALOR, SERÁ NECESSÁRIO REINICIAR",
                "&cO SERVIDOR PARA APLICAR AS MUDANÇAS!",
                "",
                "${if (unrankedOrNot) "&aCasual" else "&cCompetitivo"} &ePosição no menu: &f$position",
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
