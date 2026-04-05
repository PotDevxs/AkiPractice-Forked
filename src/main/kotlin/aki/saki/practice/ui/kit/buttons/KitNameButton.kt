/*
 * This project can	 be redistributed without
 * authorization of the developer
 *
 * Project @ AkiPractice
 * @author saki © 2026
 * Date: 11/02/2026
 */
package aki.saki.practice.ui.kit.buttons

import aki.saki.practice.PracticePlugin
import aki.saki.practice.kit.Kit
import aki.saki.practice.prompt.KitNameEditPrompt
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.conversations.ConversationFactory
import org.bukkit.conversations.NullConversationPrefix
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

class KitNameButton(
    val kit: Kit
): Button() {
    override fun getButtonItem(p0: Player): ItemStack {
        return ItemBuilder(Material.EMERALD)
            .name("&bEditar nome")
            .lore(listOf(
                "",
                "&7Clique para editar o nome",
                "",
                "&eO nome atual é: ${ChatColor.WHITE}${kit.name}",
                ""
            ))
            .build()
    }

    override fun clicked(player: Player, slot: Int, clickType: ClickType, hotbarButton: Int) {
        player.closeInventory()
        player.beginConversation(
            ConversationFactory(PracticePlugin.instance).withModality(true).withPrefix(NullConversationPrefix())
                .withFirstPrompt(KitNameEditPrompt(kit, player)).withEscapeSequence("/no").withLocalEcho(false)
                .withTimeout(25).thatExcludesNonPlayersWithMessage("Somente jogadores podem usar isso!").buildConversation(player))
    }
}
