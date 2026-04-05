package aki.saki.practice.menu

import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory

abstract class Menu {

    var isAutoUpdate = false
    var isClosedByMenu = false
    var isPlaceholder = false
    var autoUpdateTicks = 20L
    var updateAfterClick = false

    open fun getTitle(player: Player): String {
        return "Menu"
    }

    open fun getButtons(player: Player): Map<Int, Button> {
        return mutableMapOf()
    }

    open fun getSize(): Int {
        return 27
    }

    open fun size(buttons: MutableMap<Int, Button>?): Int {
        var highestSlot = -1
        buttons?.keys?.forEach { slot ->
            if (slot > highestSlot) {
                highestSlot = slot
            }
        }

        var computedSize = highestSlot + 1
        if (computedSize % 9 != 0) {
            computedSize += 9 - (computedSize % 9)
        }

        return maxOf(getSize(), computedSize)
    }

    open fun isUpdateAfterClick(): Boolean {
        return updateAfterClick
    }

    open fun onOpen(player: Player) {
    }

    open fun onClose(player: Player) {
    }

    fun openMenu(player: Player?): Inventory? {
        return MenuManager.openMenu(player, this)
    }
}
