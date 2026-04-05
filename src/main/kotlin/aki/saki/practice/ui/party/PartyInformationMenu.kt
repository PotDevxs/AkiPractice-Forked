/*
 * This project can	 be redistributed without
 * authorization of the developer
 *
 * Project @ AkiPractice
 * @author saki © 2026
 * Date: 11/02/2026
 */
package aki.saki.practice.ui.party

import com.google.common.base.Joiner
import aki.saki.practice.menu.Menu
import aki.saki.practice.menu.Button
import aki.saki.practice.party.Party
import aki.saki.practice.party.PartyType
import aki.saki.practice.utils.CC
import aki.saki.practice.utils.ItemBuilder
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.inventory.ItemStack
import java.util.stream.Collectors

/**
 * This Project is property of Zowpy © 2022
 * Redistribution of this Project is not allowed
 *
 * @author Zowpy
 * Created: 2/24/2022
 * Project: lPractice
 */

class PartyInformationMenu(private val party: Party): Menu() {

    override fun getTitle(player: Player): String {
        return "Informações do grupo"
    }

    override fun getSize(): Int {
        return 27
    }

    override fun isUpdateAfterClick(): Boolean {
        return true
    }

    override fun getButtons(player: Player): MutableMap<Int, Button> {
        val toReturn: MutableMap<Int, Button> = mutableMapOf()

        toReturn[10] = object : Button() {

            override fun getButtonItem(p0: Player): ItemStack {
                return ItemBuilder(Material.NETHER_STAR).name("${CC.PRIMARY}Líder do grupo")
                    .lore(listOf(
                        "${CC.PRIMARY}Líder: ${CC.SECONDARY}${Bukkit.getPlayer(party.leader).name}"
                    )).build()
            }
        }

        toReturn[13] = object : Button() {

            override fun getButtonItem(player: Player): ItemStack {
                return ItemBuilder(Material.HOPPER).name("${CC.PRIMARY}Privacidade")
                    .lore(listOf(
                        if (party.partyType == PartyType.PRIVATE) "&a⚫ &ePrivado" else "&7⚫ &ePrivado",
                        if (party.partyType == PartyType.PRIVATE) "&7⚫ &ePúblico" else "&a⚫ &ePúblico"
                    ))
                    .build()
            }

            override fun clicked(player: Player, slot: Int, clickType: ClickType, hotbarButton: Int) {
                if (party.leader != player.uniqueId) {
                    player.sendMessage("${CC.RED}Você não pode alterar a privacidade do grupo!")
                    return
                }

                if (party.partyType == PartyType.PRIVATE) {
                    party.partyType = PartyType.PUBLIC
                }else {
                    party.partyType = PartyType.PRIVATE
                }

                player.sendMessage("${CC.GREEN}Privacidade do grupo alterada com sucesso!")
            }

            override fun shouldUpdate(player: Player, slot: Int, clickType: ClickType): Boolean {
                return true
            }
        }

        toReturn[16] = object : Button() {

            override fun getButtonItem(p0: Player): ItemStack {
                return ItemBuilder(Material.BOOK).name("${CC.PRIMARY}Jogadores")
                    .lore(listOf(
                        "${CC.PRIMARY}Jogadores no grupo: ${CC.SECONDARY}${Joiner.on("&7, ${CC.SECONDARY}").join(party.players.stream()
                            .map { Bukkit.getPlayer(it).name }.collect(Collectors.toList()))}"
                    )).build()
            }

            override fun clicked(player: Player, slot: Int, clickType: ClickType, hotbarButton: Int) {
                PartyPlayersMenu(party).openMenu(player)
            }
        }

        return toReturn
    }
}
