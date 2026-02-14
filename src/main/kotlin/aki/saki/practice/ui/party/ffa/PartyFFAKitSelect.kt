/*
 * This project can	 be redistributed without
 * authorization of the developer
 *
 * Project @ AkiPractice
 * @author saki © 2026
 * Date: 11/02/2026
 */
package aki.saki.practice.ui.party.ffa

import aki.saki.practice.PracticePlugin
import rip.katz.api.menu.Menu
import rip.katz.api.menu.Button
import aki.saki.practice.kit.Kit
import aki.saki.practice.manager.ArenaManager
import aki.saki.practice.match.Match
import aki.saki.practice.match.impl.PartyFFAMatch
import aki.saki.practice.party.Party
import aki.saki.practice.party.PartyMatchType
import aki.saki.practice.profile.ProfileState
import aki.saki.practice.utils.CC
import aki.saki.practice.utils.ItemBuilder
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.inventory.ItemStack

/**
 * This Project is property of Zowpy © 2022
 * Redistribution of this Project is not allowed
 *
 * @author Zowpy
 * Created: 2/26/2022
 * Project: lPractice
 */

class PartyFFAKitSelect(private val party: Party): Menu() {

    override fun getTitle(p0: Player?): String {
        return "Select a kit!"
    }

    override fun getButtons(player: Player): MutableMap<Int, Button> {

        val toReturn: MutableMap<Int, Button> = mutableMapOf()

        for (kit in PracticePlugin.instance.kitManager.kits.values) {
            if (kit.boxing || kit.bedFights || kit.mlgRush || kit.bridge || kit.fireballFight) continue

            toReturn[toReturn.size] = object : Button() {

                override fun getButtonItem(p0: Player?): ItemStack {
                    return ItemBuilder(kit.displayItem).name("${CC.PRIMARY}${kit.name}").build()
                }

                override fun clicked(player: Player, slot: Int, clickType: ClickType?, hotbarButton: Int) {
                    if (clickType?.isLeftClick!!) {

                        if (party.players.size < 2) {
                            player.sendMessage("${CC.RED}You need at least 2 players to start a FFA match!")
                            return
                        }

                        val arena = ArenaManager.getFreeArena(kit)

                        if (arena == null) {
                            player.sendMessage("${CC.RED}There is no free arenas!")
                            return
                        }

                        val match = PartyFFAMatch(kit, arena)

                        for (uuid in party.players) {
                            val partyPlayer = Bukkit.getPlayer(uuid) ?: continue
                            val profile = PracticePlugin.instance.profileManager.findById(uuid)!!

                            profile.match = match.uuid
                            profile.matchObject = match
                            profile.state = ProfileState.MATCH
                            party.partyMatchType = PartyMatchType.FFA
                            match.addPlayer(partyPlayer, arena.l1!!)
                        }

                        Match.matches[match.uuid] = match

                        player.closeInventory()
                        match.start()
                    }
                }
            }
        }

        return toReturn
    }
}
