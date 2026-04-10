/*
 * This project can	 be redistributed without
 * authorization of the developer
 *
 * Project @ AkiPractice
 * @author saki © 2026
 */
package aki.saki.practice.menus

import aki.saki.practice.PracticePlugin
import aki.saki.practice.menu.Button
import aki.saki.practice.menu.Menu
import aki.saki.practice.menu.buttons.BackButton
import aki.saki.practice.profile.hotbar.Hotbar
import aki.saki.practice.utils.CC
import aki.saki.practice.utils.ItemBuilder
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.inventory.ItemStack
import kotlin.math.max

class AdminTargetLevelMenu(private val target: Player) : Menu() {

    override fun getTitle(player: Player): String =
        CC.translate("&eXP: &f${target.name}")

    override fun getSize(): Int = 27

    override fun getButtons(player: Player): Map<Int, Button> {
        val map = mutableMapOf<Int, Button>()
        map[4] = object : Button() {
            override fun getButtonItem(p: Player): ItemStack {
                val prof = profile() ?: return ItemBuilder(Material.BARRIER).name("&cOffline").build()
                return ItemBuilder(Material.EXPERIENCE_BOTTLE)
                    .name("&e${target.name}")
                    .lore(
                        "&7Nível: &f${prof.level}",
                        "&7XP: &f${prof.xp}"
                    )
                    .build()
            }
            override fun clicked(p: Player, slot: Int, clickType: ClickType, hotbarButton: Int) {}
        }
        map[10] = xpButton("&a+100 XP", Material.EMERALD, 100)
        map[11] = xpButton("&a+500 XP", Material.EMERALD_BLOCK, 500)
        map[12] = xpButton("&c-100 XP", Material.REDSTONE, -100)
        map[14] = object : Button() {
            override fun getButtonItem(p: Player): ItemStack =
                ItemBuilder(Material.LAVA_BUCKET).name("&cZerar XP").lore("&7XP e nível voltam a 0.").build()
            override fun clicked(p: Player, slot: Int, clickType: ClickType, hotbarButton: Int) {
                val prof = profile() ?: return
                prof.xp = 0L
                prof.save(true)
                refreshTarget(p)
            }
        }
        map[15] = object : Button() {
            override fun getButtonItem(p: Player): ItemStack =
                ItemBuilder(Material.GOLD_INGOT).name("&6+1 nível").lore("&7Soma 100 XP até o próximo nível cheio.").build()
            override fun clicked(p: Player, slot: Int, clickType: ClickType, hotbarButton: Int) {
                val prof = profile() ?: return
                val need = 100L - (prof.xp % 100L)
                if (need == 100L) prof.xp += 100L else prof.xp += need
                prof.save(true)
                refreshTarget(p)
            }
        }
        map[16] = object : Button() {
            override fun getButtonItem(p: Player): ItemStack =
                ItemBuilder(Material.IRON_INGOT).name("&e-1 nível").build()
            override fun clicked(p: Player, slot: Int, clickType: ClickType, hotbarButton: Int) {
                val prof = profile() ?: return
                val newLevel = max(0, prof.level - 1)
                prof.xp = newLevel * 100L
                prof.save(true)
                refreshTarget(p)
            }
        }
        map[18] = BackButton(OnlinePlayersLevelMenu())
        map[26] = object : Button() {
            override fun getButtonItem(p: Player): ItemStack =
                ItemBuilder(Material.BARRIER).name("&cFechar").build()
            override fun clicked(p: Player, slot: Int, clickType: ClickType, hotbarButton: Int) {
                p.closeInventory()
            }
        }
        return map
    }

    private fun profile() =
        if (target.isOnline) PracticePlugin.instance.profileManager.findById(target.uniqueId) else null

    private fun refreshTarget(admin: Player) {
        val prof = profile() ?: return
        target.sendMessage(CC.translate("&eSeu XP/nível foi atualizado por um administrador."))
        Hotbar.giveHotbar(prof)
        aki.saki.practice.menu.MenuManager.refresh(admin)
    }

    private fun xpButton(name: String, mat: Material, delta: Int): Button {
        return object : Button() {
            override fun getButtonItem(p: Player): ItemStack = ItemBuilder(mat).name(name).build()
            override fun clicked(p: Player, slot: Int, clickType: ClickType, hotbarButton: Int) {
                val prof = profile() ?: return
                if (delta < 0) {
                    prof.xp = max(0L, prof.xp + delta.toLong())
                } else {
                    prof.addXp(delta.toLong())
                }
                prof.save(true)
                refreshTarget(p)
            }
        }
    }
}
