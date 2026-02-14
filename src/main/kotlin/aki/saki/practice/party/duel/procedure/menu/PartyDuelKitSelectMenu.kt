/*
 * This project can	 be redistributed without
 * authorization of the developer
 *
 * Project @ AkiPractice
 * @author saki © 2026
 * Date: 11/02/2026
 */
package aki.saki.practice.party.duel.procedure.menu

import aki.saki.practice.PracticePlugin
import rip.katz.api.menu.Menu
import rip.katz.api.menu.Button
import aki.saki.practice.kit.Kit
import aki.saki.practice.manager.ArenaManager
import aki.saki.practice.party.duel.procedure.PartyDuelProcedure
import aki.saki.practice.utils.CC
import aki.saki.practice.utils.ItemBuilder
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack


/**
 * This Project is property of Zowpy © 2022
 * Redistribution of this Project is not allowed
 *
 * @author Zowpy
 * Created: 3/4/2022
 * Project: lPractice
 */

class PartyDuelKitSelectMenu: Menu() {

    override fun getTitle(p0: Player?): String {
        return "Select a kit"
    }

    override fun onClose(player: Player?) {
        if (!isClosedByMenu) {

            PartyDuelProcedure.duelProcedures.removeIf { it.issuer == player?.uniqueId }

        }
    }

    override fun getButtons(p0: Player?): MutableMap<Int, Button> {
        val toReturn: MutableMap<Int, Button> = mutableMapOf()

        for (kit in PracticePlugin.instance.kitManager.kits.values) {
            toReturn[toReturn.size] = object : Button() {
                override fun getButtonItem(p0: Player?): ItemStack {
                    return ItemBuilder(kit.displayItem)
                        .name("${CC.PRIMARY}${kit.name}")
                        .addFlags(ItemFlag.HIDE_ATTRIBUTES)
                        .build()
                }

                override fun clicked(player: Player, slot: Int, clickType: ClickType?, hotbarButton: Int) {
                    if (clickType?.isLeftClick!!) {

                        val duelProcedure = PartyDuelProcedure.getByUUID(player.uniqueId)

                        if (duelProcedure == null) {
                            player.sendMessage("${CC.RED}Something went wrong!")
                            player.closeInventory()
                            return
                        }

                        duelProcedure.kit = kit

                        if (player.hasPermission("lpractice.duel.arena")) {
                            isClosedByMenu = true
                            PartyDuelArenaSelectMenu().openMenu(player)
                        }else {
                            val arena = ArenaManager.getFreeArena(kit)

                            if (arena == null) {
                                player.sendMessage("${CC.RED}There is no free arena!")
                                isClosedByMenu = false
                                player.closeInventory()
                                return
                            }

                            duelProcedure.arena = arena
                            isClosedByMenu = true
                            player.closeInventory()

                            duelProcedure.create().send()

                            player.sendMessage("${CC.GREEN}Successfully sent duel request!")

                        }

                    }
                }
            }
        }

        return toReturn
    }
}
