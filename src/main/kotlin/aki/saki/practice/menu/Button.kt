package aki.saki.practice.menu

import aki.saki.practice.utils.ItemBuilder
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.inventory.ItemStack

abstract class Button {

    abstract fun getButtonItem(player: Player): ItemStack

    open fun clicked(player: Player, slot: Int, clickType: ClickType, hotbarButton: Int) {
    }

    open fun shouldUpdate(player: Player, slot: Int, clickType: ClickType): Boolean {
        return false
    }

    open fun shouldCancel(player: Player, slot: Int, clickType: ClickType): Boolean {
        return true
    }

    fun playNeutral(player: Player?) {
        if (player == null) {
            return
        }

        try {
            player.playSound(player.location, Sound.CLICK, 1.0F, 1.0F)
        } catch (_: Throwable) {
        }
    }

    companion object {
        @JvmStatic
        fun placeholder(material: Material, durability: Byte, name: String): Button {
            return object : Button() {
                override fun getButtonItem(player: Player): ItemStack {
                    return ItemBuilder(material)
                        .durability(durability.toInt())
                        .name(name)
                        .build()
                }
            }
        }
    }
}
