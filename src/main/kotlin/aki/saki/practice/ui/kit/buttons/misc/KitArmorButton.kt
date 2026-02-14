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
import aki.saki.practice.prompt.KitArmorPrompt
import aki.saki.practice.ui.kit.KitPresetsMenu
import org.bukkit.Material
import org.bukkit.conversations.ConversationFactory
import org.bukkit.conversations.NullConversationPrefix
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

class KitArmorButton(
    val kit: Kit
): Button() {
    override fun getButtonItem(player: Player): ItemStack {
        return ItemBuilder(Material.DIAMOND_CHESTPLATE)
            .name("&b&lKit Presets")
            .lore(listOf(
                "",
                "&7Click to see the default kit presets available to apply.",
                ""
            ))
            .build()
    }

    override fun clicked(player: Player, slot: Int, clickType: ClickType, hotbarButton: Int) {
        KitPresetsMenu(kit, player).openMenu(player)
    }
}
