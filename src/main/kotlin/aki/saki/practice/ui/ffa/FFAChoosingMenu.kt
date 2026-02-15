/*
 * This project can	 be redistributed without
 * authorization of the developer
 *
 * Project @ AkiPractice
 * @author saki © 2026
 * Date: 11/02/2026
 */
package aki.saki.practice.ui.ffa

import aki.saki.practice.PracticePlugin
import rip.katz.api.menu.Menu
import rip.katz.api.menu.Button
import aki.saki.practice.constants.Constants
import aki.saki.practice.kit.Kit
import aki.saki.practice.manager.FFAManager
import aki.saki.practice.match.ffa.FFAPlayer
import aki.saki.practice.profile.ProfileState
import aki.saki.practice.utils.CC
import aki.saki.practice.utils.ItemBuilder
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

class FFAChoosingMenu: Menu() {

    override fun getTitle(p0: Player?): String {
        return "FFA"
    }

    override fun getButtons(p0: Player?): MutableMap<Int, Button> {

        val toReturn: MutableMap<Int, Button> = mutableMapOf()

        for (kit in PracticePlugin.instance.kitManager.kits.values) {
            if (!kit.ffa || kit.build) continue

            val ffa = FFAManager.getByKit(kit)

            toReturn[toReturn.size] = object : Button() {

                override fun getButtonItem(p0: Player?): ItemStack {
                    return ItemBuilder(kit.displayItem.clone()).name("${CC.PRIMARY}${kit.name}")
                        .lore("${CC.PRIMARY}Currently playing: ${CC.SECONDARY}${ffa.players.size}")
                        .build()
                }

                override fun clicked(player: Player, slot: Int, clickType: ClickType?, hotbarButton: Int) {
                    if (clickType?.isLeftClick!!) {
                        if (aki.saki.practice.manager.FFABanManager.isBanned(player.uniqueId)) {
                            player.sendMessage(aki.saki.practice.Locale.FFA_BAN_BANNED_MSG.getMessage())
                            return
                        }
                        val profile = PracticePlugin.instance.profileManager.findById(player.uniqueId)!!

                        profile.state = ProfileState.FFA
                        profile.ffa = ffa.uuid
                        aki.saki.practice.mission.MissionManager.advance(profile, aki.saki.practice.mission.DailyMissionType.PLAY_FFA)

                        if (Constants.FFA_SPAWN != null ){
                            player.teleport(Constants.FFA_SPAWN)
                        }

                        val ffaPlayer = FFAPlayer(player.uniqueId, player.name)
                        ffa.players.add(ffaPlayer)

                        ffa.setup(ffaPlayer)
                        ffa.firstSetup(ffaPlayer)

                        player.closeInventory()
                        player.sendMessage("${CC.GREEN}Successfully joined FFA!")
                    }
                }
            }
        }

        return toReturn
    }
}
