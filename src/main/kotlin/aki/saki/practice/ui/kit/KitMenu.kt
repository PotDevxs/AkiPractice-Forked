/*
 * This project can	 be redistributed without
 * authorization of the developer
 *
 * Project @ AkiPractice
 * @author saki © 2026
 * Date: 11/02/2026
 */
package aki.saki.practice.ui.kit

import aki.saki.practice.PracticePlugin
import aki.saki.practice.kit.Kit
import aki.saki.practice.prompt.KitCreatePrompt
import org.bukkit.Material
import org.bukkit.conversations.ConversationFactory
import org.bukkit.conversations.NullConversationPrefix
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.inventory.ItemStack
import aki.saki.practice.menu.Button
import aki.saki.practice.menu.Menu
import aki.saki.practice.menu.pagination.PaginatedMenu
import aki.saki.practice.utils.CC
import aki.saki.practice.utils.ItemBuilder


/*
 * This project can't be redistributed without
 * authorization of the developer
 *
 * Project @ lPractice
 * @author yek4h © 2024
 * Date: 12/06/2024
*/

class KitMenu: PaginatedMenu() {
    override fun getPrePaginatedTitle(p0: Player): String {
        return CC.color("&bMenu de kits!")
    }

    override fun getAllPagesButtons(p0: Player): MutableMap<Int, Button> {
        val buttons = HashMap<Int, Button>()
        val sorted = PracticePlugin.instance.kitManager.getKits().sortedBy { it.name }

        for ((index, kit) in sorted.withIndex()) {
            buttons[index] = object : Button() {
                override fun getButtonItem(p0: Player): ItemStack {
                    return ItemBuilder(kit.displayItem)
                        .amount(1)
                        .name(CC.color(kit.displayName ?: kit.name))
                        .lore(CC.color(
                            listOf(
                                "",
                                "&7Clique para editar este kit!"
                            )))
                        .build()
                }

                override fun clicked(player: Player, slot: Int, clickType: ClickType, hotbarButton: Int) {
                    if (clickType.isLeftClick) {
                        KitCommandMenuEditor(kit).openMenu(player)
                    } else if (clickType.isRightClick) {
                        PracticePlugin.instance.kitManager.deleteKit(kit.name)
                        player.sendMessage(CC.color("&aO kit foi removido com sucesso!"))
                    }
                }
            }
        }
        return buttons
    }

    override fun getGlobalButtons(player: Player): MutableMap<Int, Button> {
        val buttons = HashMap<Int, Button>()

        buttons[4] = object : Button () {
            override fun getButtonItem(p0: Player): ItemStack {
                return ItemBuilder(Material.GOLD_NUGGET)
                    .name("&bClique aqui para criar um kit!")
                    .build()
            }

            override fun clicked(player: Player, slot: Int, clickType: ClickType, hotbarButton: Int) {
                player.closeInventory()
                player.beginConversation(
                    ConversationFactory(PracticePlugin.instance).withModality(true).withPrefix(NullConversationPrefix())
                        .withFirstPrompt(KitCreatePrompt(player)).withEscapeSequence("/no").withLocalEcho(false)
                        .withTimeout(25).thatExcludesNonPlayersWithMessage("Somente jogadores podem usar isso").buildConversation(player)
                )
            }
        }

        return buttons
    }
}
