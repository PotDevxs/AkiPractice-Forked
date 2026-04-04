/*
 * This project can	 be redistributed without
 * authorization of the developer
 *
 * Project @ AkiPractice
 * @author saki © 2026
 * Date: 11/02/2026
 */
package aki.saki.practice.ui.match

import rip.katz.api.menu.Menu
import rip.katz.api.menu.Button
import aki.saki.practice.match.snapshot.MatchSnapshot
import aki.saki.practice.utils.*
import org.apache.commons.lang.StringEscapeUtils
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.inventory.ItemStack
import org.bukkit.potion.PotionEffect
import rip.katz.api.menu.buttons.DisplayButton
import java.util.*
import kotlin.math.roundToInt

/**
 * This Project is property of Zowpy © 2022
 * Redistribution of this Project is not allowed
 * Created: 2/25/2022
 * Project: lPractice
 *
 * Recoded by yek4h
 *
 */

class MatchDetailsMenu(private val matchSnapshot: MatchSnapshot) : Menu() {

    override fun getTitle(player: Player?): String {
        return "Inventário de ${matchSnapshot.username}"
    }

    override fun getButtons(player: Player?): Map<Int, Button> {
        val buttonMap = mutableMapOf<Int, Button>()
        val orderedContents = InventoryUtil.fixInventoryOrder(matchSnapshot.contents)

        orderedContents.forEachIndexed { index, itemStack ->
            if (itemStack != null && itemStack.type != Material.AIR) {
                buttonMap[index] = DisplayButton(itemStack, true)
            }
        }

        matchSnapshot.armor.forEachIndexed { index, itemStack ->
            if (itemStack != null && itemStack.type != Material.AIR) {
                buttonMap[39 - index] = DisplayButton(itemStack, true)
            }
        }

        var position = 45
        buttonMap[position++] = HealthButton(matchSnapshot.health.toInt())
        buttonMap[position++] = HungerButton(matchSnapshot.hunger)
        buttonMap[position++] = EffectsButton(matchSnapshot.effects!!)

        if (matchSnapshot.shouldDisplayRemainingPotions()) {
            buttonMap[position++] = PotionsButton(matchSnapshot.username!!, matchSnapshot.getRemainingPotions())
        }

        buttonMap[position] = StatisticsButton(matchSnapshot)

        matchSnapshot.opponent?.let {
            buttonMap[53] = SwitchInventoryButton(it)
        }

        return buttonMap
    }

    private class SwitchInventoryButton(private val opponentUuid: UUID) : Button() {

        override fun getButtonItem(player: Player): ItemStack {
            val opponentSnapshot = MatchSnapshot.getByUuid(opponentUuid)

            return opponentSnapshot?.let {
                ItemBuilder(Material.LEVER)
                    .name("${CC.PRIMARY}Inventário do oponente")
                    .lore("${CC.PRIMARY}Trocar para o inventário de ${CC.SECONDARY}${it.username}${CC.PRIMARY}")
                    .build()
            } ?: ItemStack(Material.AIR)
        }

        override fun clicked(player: Player?, slot: Int, clickType: ClickType?, hotbarButton: Int) {
            val opponentSnapshot = MatchSnapshot.getByUuid(opponentUuid)

            opponentSnapshot?.let {
                player?.chat("/matchsnapshot ${it.uuid}")
            }
        }
    }

    private class HealthButton(private val healthAmount: Int) : Button() {
        override fun getButtonItem(player: Player): ItemStack {
            return ItemBuilder(Material.MELON)
                .name("${CC.PRIMARY}Vida: ${CC.SECONDARY}$healthAmount/10 ${StringEscapeUtils.unescapeJava("\u2764")}")
                .amount(if (healthAmount == 0) 1 else healthAmount)
                .build()
        }
    }

    private class HungerButton(private val hungerLevel: Int) : Button() {
        override fun getButtonItem(player: Player): ItemStack {
            return ItemBuilder(Material.COOKED_BEEF)
                .name("${CC.PRIMARY}Fome: ${CC.PRIMARY}$hungerLevel/20")
                .amount(if (hungerLevel == 0) 1 else hungerLevel)
                .build()
        }
    }

    private class EffectsButton(private val potionEffects: Collection<PotionEffect>) : Button() {
        override fun getButtonItem(player: Player): ItemStack {
            val itemBuilder = ItemBuilder(Material.POTION).name("${CC.PRIMARY}Efeitos de poção")

            if (potionEffects.isEmpty()) {
                itemBuilder.lore("${CC.PRIMARY}Sem efeitos de poção")
            } else {
                val effectLore = potionEffects.map { effect ->
                    val effectName = "${PotionUtil.getName(effect.type)} ${effect.amplifier + 1}"
                    val effectDuration = " (${TimeUtil.millisToTimer((effect.duration / 20 * 1000).toLong())})"
                    "${CC.SECONDARY}$effectName${CC.PRIMARY}$effectDuration"
                }
                itemBuilder.lore(effectLore)
            }

            return itemBuilder.build()
        }
    }

    private class PotionsButton(private val playerName: String, private val potionCount: Int) : Button() {
        override fun getButtonItem(player: Player): ItemStack {
            return ItemBuilder(Material.POTION)
                .durability(16421)
                .amount(if (potionCount == 0) 1 else potionCount)
                .name("${CC.PRIMARY}Poções")
                .lore("${CC.SECONDARY}$playerName${CC.PRIMARY} terminou com ${CC.SECONDARY}$potionCount${CC.PRIMARY} poç${if (potionCount != 1) "ões" else "ão"} restante${if (potionCount != 1) "s" else ""}.")
                .build()
        }


    }

    private class StatisticsButton(private val matchSnapshot: MatchSnapshot) : Button() {
        override fun getButtonItem(player: Player): ItemStack {
            return ItemBuilder(Material.PAPER)
                .name("${CC.PRIMARY}Estatísticas")
                .lore(
                    listOf(
                        "${CC.PRIMARY}Hits totais: ${CC.SECONDARY}${matchSnapshot.totalHits}",
                        "${CC.PRIMARY}Maior combo: ${CC.SECONDARY}${matchSnapshot.longestCombo}",
                        "${CC.PRIMARY}Nota das poções: ${PotionGradeUtil.getGrade(matchSnapshot.getPotionAccuracy())}",
                        "${CC.PRIMARY}Poções arremessadas: ${CC.SECONDARY}${matchSnapshot.potionsThrown}",
                        "${CC.PRIMARY}Poções desperdiçadas: ${CC.SECONDARY}${matchSnapshot.potionsMissed}",
                        "${CC.PRIMARY}Precisão das poções: ${CC.SECONDARY}${matchSnapshot.getPotionAccuracy()}%",
                        "&7 &7 ${CC.PRIMARY}Diversos: ",
                        "&7 &7 &7${CC.PRIMARY}Precisão do W-Tap: ${CC.SECONDARY}${matchSnapshot.wtapAccuracy}%",
                    )
                )
                .build()
        }
        fun getWTapAccuracy(): Double {
            if (matchSnapshot.wtapAttempts == 0) {
                return 0.0
            }
            return (matchSnapshot.effectiveWTaps.toDouble() / matchSnapshot.wtapAttempts * 100.0).roundToInt().toDouble()
        }
    }
}
