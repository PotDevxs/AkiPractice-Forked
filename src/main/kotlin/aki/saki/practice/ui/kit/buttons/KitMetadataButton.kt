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
import aki.saki.practice.ui.kit.KitMetadataList
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
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
 * Date: 14/06/2024
*/

class KitMetadataButton(
  val kit: Kit
) : Button() {
    override fun getButtonItem(p0: Player): ItemStack {
        return ItemBuilder(Material.ENCHANTED_BOOK)
            .enchantment(Enchantment.DURABILITY, 10)
            .name("&b&lMetadados do kit")
            .lore(listOf(
                "",
                "&7Clique para editar os metadados deste kit",
                "&7this option is for enable or disable some values",
                ""
            ))
            .build()
    }

    override fun clicked(player: Player, slot: Int, clickType: ClickType, hotbarButton: Int) {
        player.updateInventory()
        player.closeInventory()

        KitMetadataList(kit).openMenu(player)
    }
}
