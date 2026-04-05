package aki.saki.practice.menu.buttons

import aki.saki.practice.menu.Button
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.inventory.ItemStack

open class DisplayButton(
    private val itemStack: ItemStack?,
    private val cancel: Boolean
) : Button() {

    override fun getButtonItem(player: Player): ItemStack {
        return itemStack?.clone() ?: ItemStack(Material.AIR)
    }

    override fun shouldCancel(player: Player, slot: Int, clickType: ClickType): Boolean {
        return cancel
    }
}
