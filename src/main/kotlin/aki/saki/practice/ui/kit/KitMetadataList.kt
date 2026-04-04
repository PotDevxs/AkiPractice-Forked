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
import aki.saki.practice.ui.kit.buttons.KitDescriptionButton
import aki.saki.practice.ui.kit.buttons.KitInfoButton
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.inventory.ItemStack
import rip.katz.api.menu.Button
import rip.katz.api.menu.Menu
import rip.katz.api.menu.pagination.PaginatedMenu
import rip.katz.api.utils.CC
import rip.katz.api.utils.ItemBuilder


/*
 * This project can't be redistributed without
 * authorization of the developer
 *
 * Project @ lPractice
 * @author yek4h © 2024
 * Date: 13/06/2024
*/

class KitMetadataList(private val kit: Kit) : PaginatedMenu() {

    override fun getPrePaginatedTitle(p0: Player?): String {
        return CC.color("&b&lEditor de metadados do kit")
    }

    override fun isUpdateAfterClick(): Boolean {
        return true
    }

    override fun getAllPagesButtons(p0: Player?): MutableMap<Int, Button> {
        val buttons = mutableMapOf<Int, Button>()

        // Default filler button
        val fillerButton = object : Button() {
            override fun getButtonItem(p0: Player?): ItemStack {
                return ItemBuilder(Material.STAINED_GLASS_PANE)
                    .durability(7)
                    .name("").build()
            }
        }

        // Add filler buttons
        for (i in 0..35) {
            buttons[i] = fillerButton
        }

        // Add specific buttons
        buttons[1] = KitInfoButton(kit)
        buttons[10] = KitDescriptionButton(kit)

        buttons[3] = createToggleButton(Material.DIAMOND_SWORD, "&b&lModo competitivo", "&7Define se o kit pode ser jogado no competitivo para ganhar ELO", kit.ranked) {
            kit.ranked = !kit.ranked
        }

        buttons[4] = createToggleButton(Material.COOKED_BEEF, "&b&lSem fome", "&7Ativa ou desativa a fome nas partidas com este kit", kit.hunger) {
            kit.hunger = !kit.hunger
        }

        buttons[5] = createInfoButton(Material.INK_SACK, 11, "&b&lJogável em grupo", "&7Define se o kit pode ser usado em jogos de grupo", "&bEstamos trabalhando nessa função!")

        buttons[6] = createInfoButton(Material.INK_SACK, 3, "&b&lJogável em grupo FFA", "&7Define se o kit pode ser usado em FFA de grupo", "&bEstamos trabalhando nessa função!")

        buttons[7] = createToggleButton(Material.BED, "&b&lBedFight", "&7Ativa BedFight. O primeiro jogador que quebrar a cama do oponente poderá dar o abate final e vencer a partida.", kit.bedFights) {
            kit.bedFights = !kit.bedFights
        }

        buttons[12] = createInfoButton(Material.NAME_TAG, 0, "&b&lSelecionar knockback", "&7Clique para selecionar o knockback do seu kit. Isso ativa knockbacks personalizados na partida!", "&bO knockback atual deste kit é: &cnull")

        buttons[14] = createToggleButton(Material.FEATHER, "&b&lSem dano de queda", "&7Ativa ou desativa o dano de queda nas partidas com este kit.", kit.fallDamage) {
            kit.fallDamage = !kit.fallDamage
        }

        buttons[16] = createToggleButton(Material.WOOD, "&b&lModo construção", "&7Ativa o modo construção. Com isso, você pode construir na partida. &cESTE VALOR EXIGE ARENAS STANDALONE", kit.build) {
            kit.build = !kit.build
        }

        buttons[19] = KitInfoButton(kit)
        buttons[28] = KitDescriptionButton(kit)

        buttons[21] = createInfoButton(Material.WATER_LILY, 0, "&b&lBattle Rush", "", "&cWe're working on this, sorry :c")

        buttons[22] = createToggleButton(Material.DIAMOND_CHESTPLATE, "&b&lModo boxing", "&7Ativa o modo boxing para o kit. Quando boxing está ativo, o primeiro jogador a alcançar 100 hits vence :D", kit.boxing) {
            kit.boxing = !kit.boxing
        }

        buttons[23] = createInfoButton(Material.DIAMOND_HOE, 0, "&b&lModo spleef", "&7Ativa o modo spleef. Você pode derrubar seu oponente usando pás!", "&cAinda estamos trabalhando nisso :c desculpe!")

        buttons[24] = createToggleButton(Material.FENCE, "&b&lModo HCF", "&7Ativa construção na luta e os jogadores precisam prender o oponente e matá-lo para vencer", kit.hcf) {
            kit.hcf = !kit.hcf
        }

        buttons[25] = createToggleButton(Material.WATER_BUCKET, "&b&lModo MLGRush", "&7Ativa o modo MLGRush.", kit.mlgRush) {
            kit.mlgRush = !kit.mlgRush
        }

        buttons[31] = createToggleButton(Material.LEASH, "&b&lModo sumô", "&7Ativa o modo sumô. Se você cair da plataforma na água, morrerá.", kit.sumo) {
            kit.sumo = !kit.sumo
        }

        buttons[33] = object : Button() {
            override fun getButtonItem(p0: Player?): ItemStack {
                return ItemBuilder(Material.RAW_FISH).durability(3)
                    .name("&b&lDamage Ticks Value")
                    .lore(CC.color(listOf(
                        "&7Esse valor modifica o damageticks dos kits",
                        "&7Valor atual: &f${kit.damageTicks}"
                    )))
                    .build()
            }

            override fun clicked(player: Player?, slot: Int, clickType: ClickType?, hotbarButton: Int) {
                when (clickType) {
                    ClickType.LEFT -> {
                        kit.damageTicks++
                    }
                    ClickType.RIGHT -> {
                        kit.damageTicks--
                    }
                    else -> {}
                }
            }

        }

        return buttons
    }

    private fun createToggleButton(material: Material, name: String, description: String, value: Boolean, toggleAction: () -> Unit): Button {
        return object : Button() {
            override fun getButtonItem(p0: Player?): ItemStack {
                val status = if (value) "&aAtivado" else "&cDesativado"
                return ItemBuilder(material)
                    .name(CC.color(name))
                    .lore("", CC.color(description), "", "&bO valor atual é: $status", "")
                    .build()
            }

            override fun clicked(player: Player, slot: Int, clickType: ClickType, hotbarButton: Int) {
                PracticePlugin.instance.kitManager.save()
                toggleAction()
                player.sendMessage(CC.color("$name is now: ${if (value) "&aAtivado" else "&cDesativado"}"))
            }
        }
    }

    private fun createInfoButton(material: Material, durability: Int, name: String, vararg description: String): Button {
        return object : Button() {
            override fun getButtonItem(p0: Player?): ItemStack {
                return ItemBuilder(material)
                    .durability(durability)
                    .name(CC.color(name))
                    .lore(*description.map { CC.color(it) }.toTypedArray())
                    .build()
            }
        }
    }

    override fun size(buttons: MutableMap<Int, Button>?): Int {
        return 9 * 4
    }
}
