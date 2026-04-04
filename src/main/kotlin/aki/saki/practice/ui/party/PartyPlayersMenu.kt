/*
 * This project can	 be redistributed without
 * authorization of the developer
 *
 * Project @ AkiPractice
 * @author saki © 2026
 * Date: 11/02/2026
 */
package aki.saki.practice.ui.party

import rip.katz.api.menu.Button
import rip.katz.api.menu.pagination.PaginatedMenu
import aki.saki.practice.party.Party
import aki.saki.practice.PracticePlugin
import aki.saki.practice.profile.hotbar.Hotbar
import aki.saki.practice.utils.CC
import aki.saki.practice.utils.ItemBuilder
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.inventory.ItemStack

/**
 * This Project is property of Zowpy © 2022
 * Redistribution of this Project is not allowed
 * Created: 2/24/2022
 * Project: lPractice
 *
 * Recoded by yek4h
 *
 */

class PartyPlayersMenu(private val party: Party) : PaginatedMenu() {

    override fun getPrePaginatedTitle(player: Player?): String {
        return "Jogadores do grupo"
    }

    override fun getSize(): Int {
        return 36
    }

    override fun getAllPagesButtons(player: Player): MutableMap<Int, Button> {
        val buttons = mutableMapOf<Int, Button>()

        party.players.mapNotNull { Bukkit.getPlayer(it) }.forEach { partyPlayer ->
            buttons[buttons.size] = object : Button() {

                override fun getButtonItem(player: Player?): ItemStack {
                    return ItemBuilder(Material.IRON_SWORD)
                        .name("&e${partyPlayer.name}")
                        .lore(
                            listOf(
                                "",
                                "&e&o(( clique esquerdo para expulsar ))",
                                "&e&o(( clique direito para banir ))"
                            )
                        )
                        .skullBuilder()
                        .setOwner(partyPlayer.name)
                        .buildSkull()
                }

                override fun clicked(player: Player?, slot: Int, clickType: ClickType?, hotbarButton: Int) {
                    if (player?.uniqueId != party.leader) {
                        player?.sendMessage("${CC.RED}Você não pode fazer isso!")
                        return
                    }

                    val profile = PracticePlugin.instance.profileManager.findById(partyPlayer.uniqueId)!!

                    when {
                        clickType?.isRightClick == true -> {
                            party.banned.add(partyPlayer.uniqueId)
                            party.players.remove(partyPlayer.uniqueId)
                            profile.party = null
                            Hotbar.giveHotbar(profile)
                            party.sendMessage("${CC.SECONDARY}${partyPlayer.name}${CC.PRIMARY} foi banido do grupo!")
                        }
                        clickType?.isLeftClick == true -> {
                            party.players.remove(partyPlayer.uniqueId)
                            profile.party = null
                            Hotbar.giveHotbar(profile)
                            party.sendMessage("${CC.SECONDARY}${partyPlayer.name}${CC.PRIMARY} foi removido do grupo!")
                        }
                    }

                    player.closeInventory()
                    Hotbar.giveHotbar(profile)
                }
            }
        }

        return buttons
    }
}
