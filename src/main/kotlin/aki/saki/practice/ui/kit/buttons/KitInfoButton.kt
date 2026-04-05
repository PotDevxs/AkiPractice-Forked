/*
 * This project can	 be redistributed without
 * authorization of the developer
 *
 * Project @ AkiPractice
 * @author saki © 2026
 * Date: 11/02/2026
 */
package aki.saki.practice.ui.kit.buttons

import aki.saki.practice.kit.Kit
import org.bukkit.entity.Player
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

class KitInfoButton(
    val kit: Kit
): Button() {
    override fun getButtonItem(p0: Player): ItemStack {
        return ItemBuilder(kit.displayItem.type)
            .durability(kit.displayItem.durability.toInt())
            .clearLore()
            .amount(1)
            .name("&b&l${kit.name} " +
                    (if (kit.enabled) "&7(&aAtivado&7)" else "&7(&cDesativado&7)")
            ).build()
    }
}
