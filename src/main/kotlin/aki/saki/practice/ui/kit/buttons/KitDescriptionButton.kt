/*
 * This project can	 be redistributed without
 * authorization of the developer
 *
 * Project @ AkiPractice
 * @author saki © 2026
 * Date: 11/02/2026
 */
package aki.saki.practice.ui.kit.buttons

import aki.saki.practice.kit.Kit
import org.bukkit.Material
import org.bukkit.entity.Player
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

class KitDescriptionButton(
    val kit: Kit
) : Button() {
    override fun getButtonItem(player: Player): ItemStack {
        val modes = mapOf(
            "Modo competitivo" to kit.ranked,
            "Modo BedFights" to kit.bedFights,
            "Modo combo" to kit.combo,
            "Modo sumô" to kit.sumo,
            "Modo construção" to kit.build,
            "Modo HCF" to kit.hcf,
            "Modo boxing" to kit.boxing,
            "Modo FFA" to kit.ffa,
            "Modo MLGRush" to kit.mlgRush,
            "Modo bridge" to kit.bridge,
            "Modo fireball fight" to kit.fireballFight,
            "Fome" to kit.hunger,
            "Regeneração" to kit.regeneration,
            "Dano de queda" to kit.fallDamage
        )

        val modeDescriptions = modes.map { (mode, isEnabled) ->
            "&b$mode: &f${if (isEnabled) "&aSim" else "&cNão"}"
        }

        return ItemBuilder(Material.SIGN)
            .name("&b&lDescrição do kit")
            .lore(listOf(
                "",
                "&bVocê está editando este kit agora!",
                "",
                "&bNome do kit: &f${kit.name}",
                "&bNome de exibição: &f${kit.displayName}",
                "&bPosição casual: &f${kit.unrankedPosition}",
                "&bPosição competitiva: &f${kit.rankedPosition}"
            ) + modeDescriptions)
            .build()
    }
}
