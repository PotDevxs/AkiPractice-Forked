/*
 * This project can	 be redistributed without
 * authorization of the developer
 *
 * Project @ AkiPractice
 * @author saki © 2026
 * Date: 11/02/2026
 */
package aki.saki.practice.ui.party.split

import aki.saki.practice.PracticePlugin
import aki.saki.practice.kit.Kit
import rip.katz.api.menu.Menu
import rip.katz.api.menu.Button
import aki.saki.practice.manager.ArenaManager
import aki.saki.practice.manager.MatchManager
import aki.saki.practice.party.Party
import aki.saki.practice.party.PartyMatchType
import aki.saki.practice.utils.CC
import aki.saki.practice.utils.ItemBuilder
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.inventory.ItemStack

class PartySplitKitSelect(private val party: Party): Menu() {

    override fun getTitle(player: Player?): String {
        return "Select a kit!"
    }

    override fun getButtons(player: Player): MutableMap<Int, Button> {
        val buttons = mutableMapOf<Int, Button>()

        for (kit in PracticePlugin.instance.kitManager.kits.values) {
            buttons[buttons.size] = KitButton(kit, party)
        }

        return buttons
    }

    private class KitButton(
        private val kit: Kit,
        private val party: Party
    ) : Button() {

        override fun getButtonItem(player: Player?): ItemStack {
            return ItemBuilder(kit.displayItem).name("${CC.PRIMARY}${kit.name}").build()
        }

        override fun clicked(player: Player, slot: Int, clickType: ClickType?, hotbarButton: Int) {
            if (clickType?.isLeftClick == true) {
                if (party.players.size < 2) {
                    player.sendMessage("${CC.RED}You need at least 2 players to start a Split match!")
                    return
                }

                val arena = ArenaManager.getFreeArena(kit)

                if (arena == null) {
                    player.sendMessage("${CC.RED}There are no free arenas!")
                    return
                }

                player.closeInventory()
                MatchManager.createTeamMatch(kit, arena, null, true, party.players)
                party.partyMatchType = PartyMatchType.SPLIT
            }
        }
    }
}
