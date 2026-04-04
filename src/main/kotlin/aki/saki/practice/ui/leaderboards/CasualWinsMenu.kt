/*
 * This project can	 be redistributed without
 * authorization of the developer
 *
 * Project @ AkiPractice
 * @author saki © 2026
 * Date: 11/02/2026
 */
package aki.saki.practice.ui.leaderboards

import dev.ryu.core.bukkit.CoreAPI
import aki.saki.practice.PracticePlugin
import aki.saki.practice.utils.CC
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.inventory.ItemStack
import rip.katz.api.menu.Button
import rip.katz.api.menu.Menu
import rip.katz.api.utils.ItemBuilder


/*
 * This project can't be redistributed without
 * authorization of the developer
 *
 * Project @ lPractice
 * @author yek4h © 2024
 * Date: 28/06/2024
*/

class CasualWinsMenu(val p: PracticePlugin): Menu() {

    val GLASS_PANE = arrayOf(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 17, 18, 26, 27, 35, 36, 37 ,38, 39, 40, 41 ,42 ,43, 44)

    override fun getTitle(p0: Player?): String {
        return CC.translate("&7Rankings de Vitórias Casuais")
    }

    override fun getButtons(p0: Player): MutableMap<Int, Button> {
        val buttons = HashMap<Int, Button>()
        val occupiedSlots = GLASS_PANE.toMutableList()

        GLASS_PANE.forEach {

            buttons[it] = object : Button() {
                override fun getButtonItem(p0: Player?): ItemStack {
                    return ItemBuilder(Material.STAINED_GLASS_PANE)
                        .durability(7)
                        .name(" ")
                        .build()
                }
            }


        }
        buttons[3] = object : Button() {
            override fun getButtonItem(p0: Player): ItemStack {
                val lb = p.leaderboards.getTopProfilesByGlobalElo()
                    .take(10)
                    .mapIndexed { index, (profile, elo) ->
                        val bestRank = CoreAPI.grantSystem.findBestRank(CoreAPI.grantSystem.repository.findAllByPlayer(profile.uuid))
                        val color = ChatColor.valueOf(bestRank.color)
                        "${CC.PRIMARY}#${index + 1}&f. $color${profile.name} &7- &f$elo"
                            .replace("<top>", (index + 1).toString())
                            .replace("<name>", "$color${profile.name}")
                            .replace("<elo>", elo.toString())
                    }
                val lore= mutableListOf(
                    "&bSua pontuação: ${p.profileManager.findById(p0.uniqueId)!!.globalStatistic.elo}"
                )
                lore.addAll(lb)

                return ItemBuilder(Material.NETHER_STAR)
                    .name("${CC.PRIMARY}Ranking global")
                    .lore(CC.translate(lore))
                    .build()
            }


        }

        buttons[5] = object : Button() {
            override fun getButtonItem(p0: Player): ItemStack {
                return ItemBuilder(Material.SKULL_ITEM).setSkullTexture(p0.name)
                    .name("&bAlternar visualização")
                    .lore(
                        CC.translate(listOf(
                        "&7Selecione uma das visões abaixo",
                        "&7para ver outros rankings!",
                        "",
                        "&fVisão atual:",
                        "&7ELO competitivo",
                        "&a▸ Vitórias casuais",
                        "&7Vitórias competitivas",
                        "&7Melhor sequência casual diária",
                        "&7Melhor sequência competitiva diária",
                        "",
                        "&aClique para alternar!"
                    )))
                    .build()
            }

            override fun clicked(player: Player, slot: Int, clickType: ClickType?, hotbarButton: Int) {
                when (clickType) {
                    ClickType.LEFT -> {
                        player.updateInventory()
                        player.closeInventory()
                        RankedWinsMenu(p).openMenu(player)
                    }
                    ClickType.RIGHT -> {
                        player.updateInventory()
                        player.closeInventory()
                        LeaderboardRankedMenu(p).openMenu(player)
                    }
                    else -> {}
                }
            }
        }

        var slot = 0
        for (kits in p.kitManager.kits.values) {
            while (occupiedSlots.contains(slot)) {
                slot++
            }
            val lore = listOf("${CC.PRIMARY}Sua pontuação: &f${p.profileManager.findById(p0.uniqueId)!!.getKitStatistic(kits.name)!!.wins}").toMutableList()
            lore.addAll(p.leaderboards.getTopProfilesByKitWins(kits).sortedByDescending { it.second }
                .take(10)
                .mapIndexed { index, (profile, wins) ->
                    "${CC.PRIMARY}#<top>. &r<name> &7- &f<wins>"
                        .replace("<top>", (index + 1).toString())
                        .replace("<name>", "${ChatColor.valueOf(CoreAPI.grantSystem.findBestRank(CoreAPI.grantSystem.repository.findAllByPlayer(profile.uuid)).color)}${profile.name}")
                        .replace("<wins>", wins.toString())
                })
            buttons[slot] = object : Button() {
                override fun getButtonItem(p0: Player?): ItemStack {
                    return ItemBuilder(kits.displayItem)
                        .durability(kits.displayItem.durability.toInt())
                        .name(CC.translate(kits.displayName))
                        .lore(lore)
                        .build()
                }
            }
            occupiedSlots.add(slot)
        }

        return buttons
    }

    override fun size(buttons: MutableMap<Int, Button>?): Int {
        return 9 * 5
    }


}
